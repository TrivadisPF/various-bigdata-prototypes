package com.trivadis.bigdata.streamsimulator.input.csv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

/**
 * Wrapper Iterator to map the header fields to the current read CSV record data into a Spring {@link Message} object.
 * 
 * @author mzehnder
 */
public class CsvMessageIterator implements Iterator<Message<Map<String, String>>> {
    private static final Logger logger = LoggerFactory.getLogger(CsvMessageIterator.class);

    private final String[] header;
    private final Iterator<String[]> recordIterator;
    private final boolean skipEmptyLines;
    private final HeaderProvider<Map<String, String>> headerProvider;

    private long readCount;

    public CsvMessageIterator(Iterator<String[]> recordIterator, String[] header, boolean skipEmptyLines,
            HeaderProvider<Map<String, String>> headerProvider) {
        this.recordIterator = recordIterator;
        this.header = header == null ? new String[0] : header;
        this.skipEmptyLines = skipEmptyLines;
        this.headerProvider = headerProvider;
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

        Map<String, String> mapped = new LinkedHashMap<>(header.length > 0 ? header.length : 16);

        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                mapped.put(header[i], record.length > i ? record[i] : "");
            }
        } else {
            for (int i = 0; i < record.length; i++) {
                mapped.put(String.format("column_%03d", i + 1), record[i]);
            }
        }

        readCount++;

        if (readCount % 1000 == 0) {
            logger.info("Read {} CSV messages", readCount);
        }

        Map<String, ?> headersToCopy = null;
        if (headerProvider != null) {
            headersToCopy = headerProvider.getHeaders(mapped);
        }

        return MessageBuilder.withPayload(mapped).copyHeaders(headersToCopy).build();
    }

}
