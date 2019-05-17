package com.trivadis.bigdata.streamsimulator.input.csv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.trivadis.bigdata.streamsimulator.input.ColumnNameAwareConverter;
import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StaticColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * The {@link AbstractMessageSplitter} implementation to split a CSV {@link File} Message payload to individual records.
 * <p>
 * Can accept {@link String} or {@link URI} as file path, {@link File}, {@link Reader} or {@link InputStream} as payload
 * type. All other types will throw an {@link IllegalArgumentException}.
 * <p>
 * CSV configuration settings can be specified in {@link CsvProperties}.
 * 
 * TODO implement file markers as in org.springframework.integration.file.splitter.FileSplitter?
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class CsvFileMessageSplitter<T> extends AbstractMessageSplitter {

    private final CsvProperties csvCfg;
    private final ColumnNameAwareConverter<String[], T> messageConverter;
    private final HeaderProvider<T> headerProvider;

    public CsvFileMessageSplitter(CsvProperties csvCfg, ColumnNameAwareConverter<String[], T> messageConverter,
            HeaderProvider<T> headerProvider) {
        this.csvCfg = csvCfg;
        this.messageConverter = messageConverter;
        this.headerProvider = headerProvider;
    }

    @Override
    protected Object splitMessage(Message<?> message) {
        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        }
        try {

            Reader reader = null;
            Path inputPath = null;
            final Object payload = message.getPayload();
            if (payload instanceof String) {
                inputPath = new File((String) payload).toPath();
            } else if (payload instanceof URI) {
                inputPath = Paths.get((URI) payload);
            } else if (payload instanceof File) {
                inputPath = ((File) payload).toPath();
            } else if (payload instanceof InputStream) {
                reader = new InputStreamReader((InputStream) payload, csvCfg.getCharset());
            } else if (payload instanceof Reader) {
                reader = (Reader) payload;
            } else {
                throw new IllegalArgumentException("Expected File, URI, InputStream or Reader in the message payload");
            }

            if (reader == null) {
                reader = Files.newBufferedReader(inputPath, csvCfg.getCharset());
            }
            final CSVParser parser = new CSVParserBuilder()
                    .withSeparator(csvCfg.getSeparator())
                    .withQuoteChar(csvCfg.getQuoteChar())
                    .withEscapeChar(csvCfg.getEscapeChar())
                    .withIgnoreQuotations(csvCfg.isIgnoreQuotations())
                    .withIgnoreLeadingWhiteSpace(csvCfg.isIgnoreLeadingWhiteSpace())
                    .build();
            final CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(csvCfg.getSkipLines())
                    .withCSVParser(parser)
                    .build();

            // read header from file or use static header
            String[] header = null;
            if (csvCfg.isFirstLineIsHeader()) {
                header = csvReader.readNext();
                log.debug("Read CSV header: {}", (Object) header);
            } else if (csvCfg.getStaticHeader().length > 0) {
                header = csvCfg.getStaticHeader();
                log.debug("Using static header: {}", (Object) header);
            }
            ColumnNameProvider<String[]> columnNameProvider = new StaticColumnNameProvider<>(header);

            if (csvCfg.getStartIndex() > 0) {
                log.info("Fast-forwarding to record #{}...", csvCfg.getStartIndex());
                csvReader.skip(csvCfg.getStartIndex());
            }

            messageConverter.setColumnNameProvider(columnNameProvider);
            return new CsvMessageIterator<T>(csvReader.iterator(), messageConverter,
                    csvCfg.isSkipEmptyLines(), headerProvider);
        } catch (IOException e) {
            String msg = "Unable to read file: " + e.getMessage();
            log.error(msg);
            throw new MessageHandlingException(message, msg, e);
        }
    }

}