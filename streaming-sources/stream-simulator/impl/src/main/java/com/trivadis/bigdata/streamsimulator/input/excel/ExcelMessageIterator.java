package com.trivadis.bigdata.streamsimulator.input.excel;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

/**
 * Spring integration message {@link Iterator} to wrap the Excel records into a Spring {@link Message} object.<br>
 * 
 * @author Markus Zehnder
 */
public class ExcelMessageIterator<T> implements Iterator<Message<T>> {
    private static final Logger logger = LoggerFactory.getLogger(ExcelMessageIterator.class);

    private final Iterator<T> recordIterator;
    private final boolean skipEmptyLines;
    private final HeaderProvider<T> headerProvider;

    private long readCount;

    /**
     * Creates a new ExcelMessageIterator.
     * 
     * @param recordIterator the Excel record iterator which returns individual records of the input file
     * @param skipEmptyLines true = skip empty records returned from the CSV iterator
     * @param headerProvider the optional provider to set additional Spring message header fields based on the message
     *                       payload
     */
    public ExcelMessageIterator(Iterator<T> recordIterator, boolean skipEmptyLines,
            HeaderProvider<T> headerProvider) {
        this.recordIterator = recordIterator;
        this.skipEmptyLines = skipEmptyLines;
        this.headerProvider = headerProvider;
    }

    @Override
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    @Override
    public Message<T> next() {
        T record;
        // skip empty input lines
        do {
            record = recordIterator.next();
        } while (skipEmptyLines && (record == null));

        readCount++;

        if (readCount % 1000 == 0) {
            logger.info("Read {} Excel records", readCount);
        }

        Map<String, ?> headersToCopy = null;
        if (headerProvider != null) {
            headersToCopy = headerProvider.getHeaders(record);
        }

        return MessageBuilder.withPayload(record).copyHeaders(headersToCopy).build();
    }

}
