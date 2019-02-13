package com.trivadis.bigdata.streamsimulator.input.csv;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

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

        header = csvReader.readNext();
        iterator = new CsvRecordIterator(csvReader.iterator(), header);
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
