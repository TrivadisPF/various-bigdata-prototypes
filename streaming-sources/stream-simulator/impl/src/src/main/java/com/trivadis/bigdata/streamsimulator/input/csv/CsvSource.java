package com.trivadis.bigdata.streamsimulator.input.csv;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import com.opencsv.CSVReader;
import com.trivadis.bigdata.streamsimulator.input.InputSource;

/**
 * Simple CSV source POC.
 * 
 * TODO use functional interface? TODO configuration options for input file
 * encoding, separator etc.
 * 
 * @author mzehnder
 */
public class CsvSource implements InputSource {

    private CSVReader csvReader;
    private String[] header;
    private CsvRecordIterator iterator;

    public CsvSource(URI inputURI) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(inputURI));
        csvReader = new CSVReader(reader);
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
