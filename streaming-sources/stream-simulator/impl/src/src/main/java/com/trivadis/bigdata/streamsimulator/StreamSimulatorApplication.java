package com.trivadis.bigdata.streamsimulator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties;
import com.trivadis.bigdata.streamsimulator.input.InputSource;
import com.trivadis.bigdata.streamsimulator.input.csv.CsvSource;

/**
 * POC: simple message sender application prototype
 * 
 * @author mzehnder
 */
@SpringBootApplication
public class StreamSimulatorApplication implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(StreamSimulatorApplication.class);

    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    private ApplicationProperties cfg;
    
    public static void main(String[] args) {
        SpringApplication.run(StreamSimulatorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));

        if (args.containsOption("help")) {
            System.out.println("Usage: java -jar streamsimulator.jar --csv=<input-file-uri>");
            System.out.println("Example:");
            System.out.println("java -jar streamsimulator.jar --csv=file:/data/green_tripdata_2018-06.csv");
        } else if (args.containsOption("csv")) {
            List<String> csvFiles = args.getOptionValues("csv");
            URI inputURI = URI.create(csvFiles.get(0));

            // TODO create CsvSource bean in application configuration
            try (InputSource inputSource = new CsvSource(inputURI, cfg.getSource().getCsv())) {
                testSpringIntegration(inputSource);   
            }
        } else {
            logger.warn("No input source specified. Goodbye!");
        }
    }

    private void testSpringIntegration(InputSource inputSource) {
        // TODO create MessageChannel bean in application configuration
        MessageChannel channel = appContext.getBean("inboundChannel", MessageChannel.class);

        inputSource.forEach(record -> {
            channel.send(MessageBuilder.withPayload(record).build());
        });
    }

}

