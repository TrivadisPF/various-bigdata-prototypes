package com.trivadis.bigdata.streamsimulator.input;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

/**
 * {@link Converter} to convert a record value String array into a (column-name, record-value) map.<br>
 * The column names are provided by the given {@link ColumnNameProvider}.
 * 
 * @author Markus Zehnder
 */
public class StringArrayToMapConverter implements Converter<String[], Map<String, String>> {

    private final ColumnNameProvider<?> columnNameProvider;

    public StringArrayToMapConverter(ColumnNameProvider<?> columnNameProvider) {
        this.columnNameProvider = columnNameProvider;
    }

    @Override
    public Map<String, String> convert(String[] record) {
        String[] header = columnNameProvider.getColumnNames();

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

        return mapped;
    }

}
