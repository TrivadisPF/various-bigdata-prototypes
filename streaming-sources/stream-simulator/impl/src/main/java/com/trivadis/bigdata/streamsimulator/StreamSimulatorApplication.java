package com.trivadis.bigdata.streamsimulator;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationConfig;
import com.trivadis.bigdata.streamsimulator.cfg.ApplicationConfig.InputFile;

import lombok.extern.slf4j.Slf4j;

/**
 * POC: simple message sender application prototype
 * 
 * @author Markus Zehnder
 */
@SpringBootApplication
@Slf4j
public class StreamSimulatorApplication implements ApplicationRunner {

    @Autowired
    private InputFile inputFile;

    @Autowired
    private ApplicationConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(StreamSimulatorApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));

        // TODO use https://commons.apache.org/proper/commons-cli/ for more powerful cli parsing & help messages
        if (args.containsOption("help")) {
            System.out.println("Usage: java -jar streamsimulator.jar INPUT");
            System.out.println();
            System.out.println("INPUT options:");
            System.out.println("  --csv                        CSV input file");
            System.out.println("  --xsl                        Excel input file");
            System.out.println("  --simulator.input-directory  scan directory for *.csv");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("java -jar streamsimulator.jar --csv=/data/green_tripdata_2018-06.csv");
            System.out.println("java -jar streamsimulator.jar --simulator.input-directory=/data");
            return;
        }

        if (args.containsOption("csv")) {
            inputFile.process(new File(args.getOptionValues("csv").get(0)), ApplicationConfig.FILETYPE_CSV);
        } else if (args.containsOption("xsl")) {
            inputFile.process(new File(args.getOptionValues("xsl").get(0)), ApplicationConfig.FILETYPE_EXCEL);
        } else {
            appConfig.startInputFilePolling();
            // appConfig.stopInputFilePolling(); // also closes input file reader!
        }
    }

}
