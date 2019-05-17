package com.trivadis.bigdata.streamsimulator.input.excel;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameAwareConverter;
import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StaticColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * The {@link AbstractMessageSplitter} implementation to split an Excel {@link File} Message payload to individual
 * records.
 * <p>
 * Can accept {@link String} or {@link URI} as file path or a {@link File} as payload type. All other types will throw
 * an {@link IllegalArgumentException}.
 * <p>
 * Excel configuration settings can be specified in {@link ExcelProperties}.
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class ExcelFileMessageSplitter<T> extends AbstractMessageSplitter {

    private final ExcelProperties excelCfg;
    private final ColumnNameAwareConverter<RowSet, T> messageConverter;
    private final HeaderProvider<T> headerProvider;

    public ExcelFileMessageSplitter(ExcelProperties excelCfg, ColumnNameAwareConverter<RowSet, T> messageConverter,
            HeaderProvider<T> headerProvider) {
        this.excelCfg = excelCfg;
        this.messageConverter = messageConverter;
        this.headerProvider = headerProvider;
    }

    @Override
    protected Object splitMessage(Message<?> message) {
        if (log.isDebugEnabled()) {
            log.debug(message.toString());
        }
        try {

            File inputFile;
            final Object payload = message.getPayload();
            if (payload instanceof String) {
                inputFile = new File((String) payload);
            } else if (payload instanceof URI) {
                inputFile = new File((URI) payload);
            } else if (payload instanceof File) {
                inputFile = (File) payload;
            } else {
                throw new IllegalArgumentException("Expected File or URI in the message payload");
            }

            // read header from file or use static header
            ColumnNameProvider<RowSet> columnNameProvider;
            if (excelCfg.isFirstLineIsHeader()) {
                columnNameProvider = new RowNumberColumnNameProvider(excelCfg.getSkipLines());
                log.debug("Reading column names from Excel file");
            } else {
                columnNameProvider = new StaticColumnNameProvider<>(excelCfg.getStaticHeader());
                log.debug("Using static header: {}", (Object) columnNameProvider.getColumnNames());
            }

            messageConverter.setColumnNameProvider(columnNameProvider);

            final ExcelReader<T> excelReader = new ExcelReader<>(inputFile, messageConverter,
                    columnNameProvider);
            excelReader.open();

            if (excelCfg.getStartIndex() > 0) {
                log.info("Fast-forwarding to record #{}...", excelCfg.getStartIndex());
                excelReader.skip(excelCfg.getStartIndex());
            }

            return new ExcelMessageIterator<T>(excelReader.iterator(), excelCfg.isSkipEmptyLines(),
                    headerProvider);

        } catch (IOException e) {
            String msg = "Unable to read file: " + e.getMessage();
            log.error(msg);
            throw new MessageHandlingException(message, msg, e);
        }
    }

}