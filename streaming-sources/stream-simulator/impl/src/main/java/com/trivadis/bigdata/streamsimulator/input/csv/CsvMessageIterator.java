package com.trivadis.bigdata.streamsimulator.input.csv;

import java.util.Iterator;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring integration message {@link Iterator} to wrap the CSV records into a Spring {@link Message} object.<br>
 * The returned CSV record from the CSV record iterator is converted to a message payload data object by the given
 * {@link Converter}.
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class CsvMessageIterator<T> implements Iterator<Message<T>> {

    private final Iterator<String[]> recordIterator;
    private final Converter<String[], T> recordConverter;
    private final boolean skipEmptyLines;
    private final HeaderProvider<T> headerProvider;

    private long readCount;

    /**
     * Creates a new CsvMessageIterator.
     * 
     * @param recordIterator  the CSV record iterator which returns individual records of the input CSV file
     * @param recordConverter the CSV record converter to the desired Spring message payload object
     * @param skipEmptyLines  true = skip empty records returned from the CSV iterator
     * @param headerProvider  the optional provider to set additional Spring message header fields based on the message
     *                        payload
     */
    public CsvMessageIterator(Iterator<String[]> recordIterator, Converter<String[], T> recordConverter,
            boolean skipEmptyLines,
            HeaderProvider<T> headerProvider) {
        this.recordIterator = recordIterator;
        this.recordConverter = recordConverter;
        this.skipEmptyLines = skipEmptyLines;
        this.headerProvider = headerProvider;
    }

    @Override
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    @Override
    public Message<T> next() {

        String[] record;
        // skip empty input lines
        do {
            record = recordIterator.next();
        } while (skipEmptyLines && (record.length == 0 || record.length == 1 && record[0].isEmpty()));

        T mapped = recordConverter.convert(record);

        readCount++;

        if (readCount % 1000 == 0) {
            log.info("Read {} CSV messages", readCount);
        }

        Map<String, ?> headersToCopy = null;
        if (headerProvider != null) {
            headersToCopy = headerProvider.getHeaders(mapped);
        }

        return MessageBuilder.withPayload(mapped).copyHeaders(headersToCopy).build();
    }

}
