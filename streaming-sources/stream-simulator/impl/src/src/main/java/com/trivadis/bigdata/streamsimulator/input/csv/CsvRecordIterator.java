package com.trivadis.bigdata.streamsimulator.input.csv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper Iterator to map the header fields to the current read CSV record
 * data.
 * 
 * @author mzehnder
 */
public class CsvRecordIterator implements Iterator<Map<String, String>> {
    private String[] header;
    private Iterator<String[]> recordIterator;

    public CsvRecordIterator(Iterator<String[]> recordIterator, String[] header) {
        this.recordIterator = recordIterator;
        this.header = header;
    }

    @Override
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    @Override
    public Map<String, String> next() {

        String[] record;
        // skip empty input lines
        do {
            record = recordIterator.next();
        } while (record.length == 0);

        Map<String, String> mapped = new HashMap<>();

        for (int i = 0; i < header.length; i++) {
            mapped.put(header[i], record.length > i ? record[i] : "");
        }

        return mapped;
    }

}
