package com.trivadis.bigdata.streamsimulator.input.csv;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties;
import com.trivadis.bigdata.streamsimulator.input.InputSource;

/**
 * Simple CSV source POC.
 * 
 * TODO use Spring Integration
 * 
 * @author mzehnder
 */
public class CsvSource implements InputSource {
    private static final Logger logger = LoggerFactory.getLogger(CsvSource.class);

    private CSVReader csvReader;
    private String[] header;
    private CsvRecordIterator iterator;

    public CsvSource(URI inputURI, ApplicationProperties.Csv cfg) throws IOException {
        final Reader reader = Files.newBufferedReader(Paths.get(inputURI), cfg.getCharset());

        final CSVParser parser = new CSVParserBuilder()
                .withSeparator(cfg.getSeparator())
                .withQuoteChar(cfg.getQuoteChar())
                .withEscapeChar(cfg.getEscapeChar())
                .withIgnoreQuotations(cfg.isIgnoreQuotations())
                .withIgnoreLeadingWhiteSpace(cfg.isIgnoreLeadingWhiteSpace())
                .build();
        csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(cfg.getSkipLines())
                .withCSVParser(parser)
                .build();

        // read header from file or use static header
        if (cfg.isFirstLineIsHeader()) {
            header = csvReader.readNext();
            logger.debug("Read CSV header: {}", (Object)header);
        } else if (cfg.getStaticHeader().length > 0) {
            header = cfg.getStaticHeader();
            logger.debug("Using static header: {}", (Object)header);
        }

        if (cfg.getStartIndex() > 0) {
            logger.info("Fast-forwarding to record #{}...", cfg.getStartIndex());
            csvReader.skip(cfg.getStartIndex());
        }

        iterator = new CsvRecordIterator(csvReader.iterator(), header, cfg.isSkipEmptyLines());
    }

    public String[] getHeader() {
        return header;
    }

    @Override
    public Iterator<Map<String, String>> iterator() {
        return iterator;
    }

    @Override
    public void close() throws IOException {
        csvReader.close();
    }
}
