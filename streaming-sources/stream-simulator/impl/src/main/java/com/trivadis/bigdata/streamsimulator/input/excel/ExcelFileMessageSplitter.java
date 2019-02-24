package com.trivadis.bigdata.streamsimulator.input.excel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StaticColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.transform.HeaderProvider;

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
public class ExcelFileMessageSplitter extends AbstractMessageSplitter {
    private static final Logger log = LoggerFactory.getLogger(ExcelFileMessageSplitter.class);

    private final ExcelProperties excelCfg;
    private final HeaderProvider<Map<String, String>> headerProvider;

    public ExcelFileMessageSplitter(ExcelProperties excelCfg, HeaderProvider<Map<String, String>> headerProvider) {
        this.excelCfg = excelCfg;
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

            Converter<RowSet, Map<String, String>> rowMapper = new RowSetToMapConverter(columnNameProvider);

            final ExcelReader<Map<String, String>> excelReader = new ExcelReader<>(inputFile, rowMapper,
                    columnNameProvider);
            excelReader.open();

            if (excelCfg.getStartIndex() > 0) {
                log.info("Fast-forwarding to record #{}...", excelCfg.getStartIndex());
                excelReader.skip(excelCfg.getStartIndex());
            }

            return new ExcelMessageIterator<Map<String, String>>(excelReader.iterator(), excelCfg.isSkipEmptyLines(),
                    headerProvider);
        } catch (IOException e) {
            String msg = "Unable to read file: " + e.getMessage();
            log.error(msg);
            throw new MessageHandlingException(message, msg, e);
        }
    }

}