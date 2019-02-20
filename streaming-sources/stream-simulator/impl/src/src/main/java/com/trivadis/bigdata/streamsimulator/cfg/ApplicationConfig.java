package com.trivadis.bigdata.streamsimulator.cfg;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;
import com.trivadis.bigdata.streamsimulator.input.csv.CsvFileMessageSplitter;
import com.trivadis.bigdata.streamsimulator.msg.MsgHeader;
import com.trivadis.bigdata.streamsimulator.transform.CsvDelayHeaderProvider;
import com.trivadis.bigdata.streamsimulator.transform.TransformDates;

/**
 * Common application configuration of message channels and flows.
 * 
 * @author mzehnder
 */
@Configuration
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /**
     * Unfortunately we still have to use String identifiers for channel identifications, e.g. in @Gateway annotations,
     * instead of just calling fileInputChannel()...
     */
    private final static String FILE_INPUT_CHANNEL_NAME = "fileInputChannel";

    private final static String INPUT_FILE_POLLER_NAME = "input-file-poller";

    @Autowired
    private ApplicationProperties cfg;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * Inbound channel for all input files to process in the simulator.
     */
    @Bean(FILE_INPUT_CHANNEL_NAME)
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    /**
     * Single global outbound channel for configured destination in "${simulator.output}".
     * 
     * TODO split to destination specific channels to allow custom pre-send processing per destination (e.g. add
     * destination specific headers)
     */
    @Bean
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

    /**
     * Input file messaging gateway to manually feed an input file to use in the simulator.
     */
    @MessagingGateway
    public interface InputFile {

        @Gateway(requestChannel = FILE_INPUT_CHANNEL_NAME)
        void process(File file, @Header("filetype") String fileType);
    }

    /**
     * Optional input file poller to use in the simulator. Must be started manually.
     */
    @Bean
    public IntegrationFlow inputFilePollerFlow() {
        return IntegrationFlows
                .from(Files.inboundAdapter(cfg.getInputDirectory())
                        .patternFilter("*.csv"),
                        e -> e.poller(Pollers.fixedDelay(5000))
                                .id(INPUT_FILE_POLLER_NAME)
                                .autoStartup(false)
                                .role("input-poller-group")) // TODO figure out role concept
                .channel(fileInputChannel())

// testing shutdown after initial poll
//                .publishSubscribeChannel(c -> c
//                        .subscribe(s -> s.channel(fileInputChannel()))
//                        .subscribe(s -> s.log(LoggingHandler.Level.WARN, null,
//                                "headers['file_originalFile'].absolutePath + ' transferred'"))) // TODO add shutdown trigger

                .get();
    }

    /**
     * Input files flow: read input files from file input channel
     */
    @Bean
    public IntegrationFlow inputFilesFlow() {

        IntegrationFlowBuilder builder = IntegrationFlows
                .from(fileInputChannel())
                .log(LoggingHandler.Level.INFO)
                // TODO add router for different file types - at the moment we can only handle CSV files
                .split(csvFileMessageSplitter(cfg.getReferenceTimestamp(), cfg.getSpeedup()));

        builder = addMessageDateFieldTransformer(builder);
        builder = addMessageTimeFlux(builder);
        builder = addThrottling(builder);

        return builder
                .channel(outboundChannel())
                .get();
    }

    @Bean
    public CsvFileMessageSplitter csvFileMessageSplitter(long referenceTimestamp, Speedup speedup) {
        CsvDelayHeaderProvider headerProvider = speedup.isEnabled()
                ? new CsvDelayHeaderProvider(referenceTimestamp, speedup)
                : null;
        return new CsvFileMessageSplitter(cfg.getSource().getCsv(), headerProvider);
    }

    IntegrationFlowBuilder addMessageDateFieldTransformer(IntegrationFlowBuilder builder) {
        if (!cfg.getAdjustDates().isEnabled()) {
            return builder;
        }

        // TODO support multiple different date fields (date / dateTime / time zones)
        return builder.transform(new TransformDates(cfg.getAdjustDuration(), cfg.getAdjustDates()));
    }

    IntegrationFlowBuilder addMessageTimeFlux(IntegrationFlowBuilder builder) {
        if (!cfg.getSpeedup().isEnabled()) {
            return builder;
        }

        if (cfg.getSpeedup().isSimpleMode()) {
            logger.warn("Using simple in-memory delayer, all messages will be loaded into memory!");
            return builder
                    .delay("delayer.messageGroupId", d -> d.delayExpression("headers['" + MsgHeader.DELAY + "']"));

        } else {
            logger.error("FIXME Only simple speedup handling with reading all messages into memory is implemented.");
            System.exit(1);
        }
        return builder;
    }

    // quick and dirty throttling test
    // TODO shutdown poller after last msg, otherwise application keeps running (use an Advice?)
    IntegrationFlowBuilder addThrottling(IntegrationFlowBuilder builder) {
        if (!cfg.getThrottling().isEnabled()) {
            return builder;
        }

        logger.info("Enabled throttling with max: {} msg / {} ms", cfg.getThrottling().getMaxMessagesPerPoll(),
                cfg.getThrottling().getFixedDelay());

        return builder
                .channel(new QueueChannel(cfg.getThrottling().getMaxMessagesPerPoll() * 2))
                .bridge(e -> e.poller(Pollers.fixedDelay(cfg.getThrottling().getFixedDelay())
                        .maxMessagesPerPoll(cfg.getThrottling().getMaxMessagesPerPoll()))
                        .id("throttling-poller")
                        .role("input-poller-group"));
    }

    @Bean
    public IntegrationFlow controlBusFlow() {
        return IntegrationFlows.from(ControlBusGateway.class).controlBus().get();
    }

    // TODO investigate ControlBus
    public interface ControlBusGateway {
        void send(String command);
    }

    public void startInputFilePolling() {
        // TODO there needs to be a better way...
        Lifecycle poller = applicationContext.getBean(ApplicationConfig.INPUT_FILE_POLLER_NAME, Lifecycle.class);
        poller.start();
    }

    public void stopInputFilePolling() {
        Lifecycle poller = applicationContext.getBean(ApplicationConfig.INPUT_FILE_POLLER_NAME, Lifecycle.class);
        poller.stop();
    }
}
