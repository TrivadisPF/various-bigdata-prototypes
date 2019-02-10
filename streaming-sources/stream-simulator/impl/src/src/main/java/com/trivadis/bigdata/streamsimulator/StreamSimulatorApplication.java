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

import com.trivadis.bigdata.streamsimulator.input.InputSource;
import com.trivadis.bigdata.streamsimulator.input.csv.CsvSource;
import com.trivadis.bigdata.streamsimulator.output.KafkaProducer;

/**
 * POC: simple Kafka message sender application for some simple tests
 * 
 * @author mzehnder
 */
@SpringBootApplication
public class StreamSimulatorApplication implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(StreamSimulatorApplication.class);

    @Autowired
    KafkaProducer producer;

    public static void main(String[] args) {
        SpringApplication.run(StreamSimulatorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));

        if (args.containsOption("help")) {
            System.out.println("Usage: java -jar streamsimulator.jar --csv=<input-file-uri>");
        } else if (args.containsOption("csv")) {
            List<String> csvFiles = args.getOptionValues("csv");
            URI inputURI = URI.create(csvFiles.get(0));

            try (InputSource inputSource = new CsvSource(inputURI)) {
                process(inputSource);
            }
        } else {
            logger.warn("No input source specified. Goodbye!");
        }
    }

    private void process(InputSource inputSource) {
        inputSource.forEach(record -> {
            producer.sendMessage(record.toString());
        });
    }

}
