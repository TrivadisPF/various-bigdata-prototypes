package com.trivadis.bigdata.streamsimulator;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import com.trivadis.bigdata.streamsimulator.input.InputSource;

/**
 * POC: simple message sender application prototype
 * 
 * @author mzehnder
 */
@SpringBootApplication
public class StreamSimulatorApplication implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(StreamSimulatorApplication.class);

    @Autowired
    private MessageChannel inboundChannel;

    @Autowired(required = false)
    private InputSource inputSource;

    public static void main(String[] args) {
        SpringApplication.run(StreamSimulatorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));

        if (args.containsOption("help") || inputSource == null) {
            System.out.println("Usage: java -jar streamsimulator.jar --csv=<input-file-uri>");
            System.out.println("Example:");
            System.out.println("java -jar streamsimulator.jar --csv=file:/data/green_tripdata_2018-06.csv");
            return;
        }

        testSpringIntegration();
    }

    private void testSpringIntegration() {
        inputSource.forEach(record -> {
            inboundChannel.send(MessageBuilder.withPayload(record).build());
        });
    }

}
