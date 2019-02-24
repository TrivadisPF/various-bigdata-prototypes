package com.trivadis.bigdata.streamsimulator.input.excel;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StringArrayToMapConverter;

/**
 * {@link Converter} to convert a {@link RowSet} into a (column name, record value) map.<br>
 * The column names are provided by the given {@link ColumnNameProvider}.
 *
 * @author Markus Zehnder
 */
public class RowSetToMapConverter implements Converter<RowSet, Map<String, String>> {

    private final StringArrayToMapConverter converter;

    public RowSetToMapConverter(ColumnNameProvider<?> columnNameProvider) {
        this.converter = new StringArrayToMapConverter(columnNameProvider);
    }

    @Override
    public Map<String, String> convert(RowSet rs) {
        return converter.convert(rs.getCurrentRow());
    }

}
