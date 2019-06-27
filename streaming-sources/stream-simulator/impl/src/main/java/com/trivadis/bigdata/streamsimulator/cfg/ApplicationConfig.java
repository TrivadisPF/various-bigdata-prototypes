package com.trivadis.bigdata.streamsimulator.cfg;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.avro.Schema;
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
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;
import com.trivadis.bigdata.streamsimulator.cfg.KafkaProperties.Avro;
import com.trivadis.bigdata.streamsimulator.input.ColumnNameAwareConverter;
import com.trivadis.bigdata.streamsimulator.input.StringArrayToGenericAvroConverter;
import com.trivadis.bigdata.streamsimulator.input.StringArrayToMapConverter;
import com.trivadis.bigdata.streamsimulator.input.csv.CsvFileMessageSplitter;
import com.trivadis.bigdata.streamsimulator.input.excel.ExcelFileMessageSplitter;
import com.trivadis.bigdata.streamsimulator.input.excel.RowSet;
import com.trivadis.bigdata.streamsimulator.input.excel.RowSetToMapConverter;
import com.trivadis.bigdata.streamsimulator.msg.MessageDelayer;
import com.trivadis.bigdata.streamsimulator.transform.AvroGenericMsgDelayHeaderProvider;
import com.trivadis.bigdata.streamsimulator.transform.MapMsgDelayHeaderProvider;
import com.trivadis.bigdata.streamsimulator.transform.TransformDates;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.extern.slf4j.Slf4j;

/**
 * Common application configuration of message channels and flows.
 * 
 * @author Markus Zehnder
 */
@Configuration
@Slf4j
public class ApplicationConfig {

    public static final String FILETYPE_EXCEL = "XSL";
    public static final String FILETYPE_CSV = "CSV";

    /**
     * Unfortunately we still have to use String identifiers for channel identifications, e.g. in @Gateway annotations,
     * instead of just calling fileInputChannel()...
     */
    private final static String FILE_INPUT_CHANNEL_NAME = "fileInputChannel";

    private final static String INPUT_FILE_POLLER_NAME = "input-file-poller";

    private final static String CSV_INPUT_CHANNEL_NAME = "csvInputChannel";
    private final static String XSL_INPUT_CHANNEL_NAME = "xslInputChannel";

    private final static String FILE_RECORD_CHANNEL_NAME = "fileRecordChannel";

    private final static String HEADER_FILETYPE = "filetype";

    @Autowired
    private ApplicationProperties cfg;

    @Autowired
    private KafkaProperties kafkaCfg;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @return Inbound channel for all input files to process in the simulator.
     */
    @Bean(FILE_INPUT_CHANNEL_NAME)
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    /**
     * @return Specific message channel for CSV input file processing.
     */
    @Bean(CSV_INPUT_CHANNEL_NAME)
    public MessageChannel csvInputChannel() {
        return new DirectChannel();
    }

    /**
     * @return Specific message channel for Excel input file processing.
     */
    @Bean(XSL_INPUT_CHANNEL_NAME)
    public MessageChannel excelInputChannel() {
        return new DirectChannel();
    }

    /**
     * @return Message channel for all read records from the input files.
     */
    @Bean(FILE_RECORD_CHANNEL_NAME)
    public MessageChannel fileRecordChannel() {
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
        void process(File file, @Header(HEADER_FILETYPE) String fileType);
    }

    /**
     * Optional input file poller to use in the simulator. Must be started manually.
     */
    @Bean
    public IntegrationFlow inputFilePollerFlow() {
        // TODO include Excel files, enhance header with specific file type
        return IntegrationFlows
                .from(Files.inboundAdapter(cfg.getInputDirectory())
                        .patternFilter("*.csv"),
                        e -> e.poller(Pollers.fixedDelay(5000))
                                .id(INPUT_FILE_POLLER_NAME)
                                .autoStartup(false)
                                .role("input-poller-group")) // TODO figure out role concept
                .enrichHeaders(Collections.singletonMap(HEADER_FILETYPE, FILETYPE_CSV))
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
        return IntegrationFlows
                .from(fileInputChannel())
                .log(LoggingHandler.Level.INFO)
                .route(inputFileTypeRouter())
                .get();
    }

    public HeaderValueRouter inputFileTypeRouter() {
        HeaderValueRouter router = new HeaderValueRouter(HEADER_FILETYPE);
        router.setChannelMapping(FILETYPE_CSV, CSV_INPUT_CHANNEL_NAME); // too bad channel can only be configured by
                                                                        // name...
        router.setChannelMapping(FILETYPE_EXCEL, XSL_INPUT_CHANNEL_NAME);
        return router;
    }

    @Bean
    public IntegrationFlow fileRecordsFlow() {
        IntegrationFlowBuilder builder = IntegrationFlows
                .from(fileRecordChannel());

        addMessageDateFieldTransformer(builder);
        new MessageDelayer(cfg.getSpeedup()).build(builder);
        addThrottling(builder);

        return builder
                .channel(outboundChannel())
                .get();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public CsvFileMessageSplitter<?> csvFileMessageSplitter(long referenceTimestamp, Speedup speedup) {

        Avro avroCfg = kafkaCfg.getAvro();
        if (avroCfg.isEnabled()) {
            // FIXME push schema initialization into message splitter when we know the input file!
            Schema schema = getAvroSchema();

            StringArrayToGenericAvroConverter messageConverter = new StringArrayToGenericAvroConverter(schema);

            AvroGenericMsgDelayHeaderProvider headerProvider = speedup.isEnabled()
                    ? new AvroGenericMsgDelayHeaderProvider(referenceTimestamp, speedup)
                    : null;
            return new CsvFileMessageSplitter(cfg.getSource().getCsv(), messageConverter, headerProvider);
        } else {
            StringArrayToMapConverter messageConverter = new StringArrayToMapConverter();

            MapMsgDelayHeaderProvider headerProvider = speedup.isEnabled()
                    ? new MapMsgDelayHeaderProvider(referenceTimestamp, speedup)
                    : null;
            return new CsvFileMessageSplitter(cfg.getSource().getCsv(), messageConverter, headerProvider);
        }
    }

    public ExcelFileMessageSplitter<?> excelFileMessageSplitter(long referenceTimestamp, Speedup speedup) {
        // FIXME add Excel to Avro support
        Avro avroCfg = kafkaCfg.getAvro();
        if (avroCfg.isEnabled()) {
            // FIXME push schema initialization into message splitter when we know the input file!
            Schema schema = getAvroSchema();

            StringArrayToGenericAvroConverter messageConverter = new StringArrayToGenericAvroConverter(schema);

            AvroGenericMsgDelayHeaderProvider headerProvider = speedup.isEnabled()
                    ? new AvroGenericMsgDelayHeaderProvider(referenceTimestamp, speedup)
                    : null;
            return new ExcelFileMessageSplitter(cfg.getSource().getExcel(), messageConverter, headerProvider);
        } else {
            ColumnNameAwareConverter<RowSet, Map<String, String>> messageConverter = new RowSetToMapConverter();

        MapMsgDelayHeaderProvider headerProvider = speedup.isEnabled()
                ? new MapMsgDelayHeaderProvider(referenceTimestamp, speedup)
                : null;
        return new ExcelFileMessageSplitter(cfg.getSource().getExcel(), messageConverter, headerProvider);
        }
    }

    public Schema getAvroSchema() {
        Avro avroCfg = kafkaCfg.getAvro();
        // FIXME hard coded subject for testing only!
        // Implement subject provider (e.g. based on file name conventions or by cfg property)...
        String subject = "nyc_green_taxi_trip_data-value";

        try {
            Schema.Parser parser = new Schema.Parser();

            if (avroCfg.isSchemaRegistry()) {
                log.info("Downloading latest Avro metadata for {} from {}.", subject, avroCfg.getSchemaRegistryUrls());
                CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(
                        avroCfg.getSchemaRegistryUrls(), avroCfg.getIdentityMapCapacity());

                SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(subject);
                return parser.parse(schemaMetadata.getSchema());
            } else {
                subject = "/projects/personal/github/various-bigdata-prototypes/streaming-sources/stream-simulator/impl/data/nyc_green_taxi_tripdata.avsc";
                return new Schema.Parser().parse(new File(subject));
            }
        } catch (RestClientException | IOException e) {
            throw new RuntimeException(
                    String.format("Error downloading Avro metadata for %s from %s.", subject, avroCfg.getSchemaRegistryUrls()), e);
        }
    }

    @Bean
    public IntegrationFlow csvToRecordsFlow() {
        IntegrationFlowBuilder builder = IntegrationFlows
                .from(csvInputChannel())
                .split(csvFileMessageSplitter(cfg.getReferenceTimestamp(), cfg.getSpeedup()))
                .channel(fileRecordChannel());

        return builder.get();
    }

    @Bean
    public IntegrationFlow excelToRecordsFlow() {
        IntegrationFlowBuilder builder = IntegrationFlows
                .from(excelInputChannel())
                .split(excelFileMessageSplitter(cfg.getReferenceTimestamp(), cfg.getSpeedup()))
                .channel(fileRecordChannel());

        return builder.get();
    }

    IntegrationFlowBuilder addMessageDateFieldTransformer(IntegrationFlowBuilder builder) {
        if (!cfg.getAdjustDates().isEnabled()) {
            return builder;
        }

        // TODO support multiple different date fields (date / dateTime / time zones)
        return builder.transform(new TransformDates(cfg.getAdjustDuration(), cfg.getAdjustDates()));
    }

    // quick and dirty throttling test
    // TODO shutdown poller after last msg, otherwise application keeps running (use an Advice?)
    IntegrationFlowBuilder addThrottling(IntegrationFlowBuilder builder) {
        if (!cfg.getThrottling().isEnabled()) {
            return builder;
        }

        log.info("Enabled throttling with max: {} msg / {} ms", cfg.getThrottling().getMaxMessagesPerPoll(),
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
