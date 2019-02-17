package com.trivadis.bigdata.streamsimulator.input.csv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * Wrapper Iterator to map the header fields to the current read CSV record data into a Spring {@link Message} object.
 * 
 * @author mzehnder
 */
public class CsvMessageIterator implements Iterator<Message<Map<String, String>>> {
    private final String[] header;
    private final Iterator<String[]> recordIterator;
    private final boolean skipEmptyLines;

    public CsvMessageIterator(Iterator<String[]> recordIterator, String[] header, boolean skipEmptyLines) {
        this.recordIterator = recordIterator;
        this.header = header == null ? new String[0] : header;
        this.skipEmptyLines = skipEmptyLines;
    }

    @Override
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    @Override
    public Message<Map<String, String>> next() {

        String[] record;
        // skip empty input lines
        do {
            record = recordIterator.next();
        } while (skipEmptyLines && (record.length == 0 || record.length == 1 && record[0].isEmpty()));

        Map<String, String> mapped = new LinkedHashMap<>();

        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                mapped.put(header[i], record.length > i ? record[i] : "");
            }
        } else {
            for (int i = 0; i < record.length; i++) {
                mapped.put(String.format("column_%03d", i + 1), record[i]);
            }
        }

        // TODO set header fields
        return MessageBuilder.withPayload(mapped).build();
    }

}
