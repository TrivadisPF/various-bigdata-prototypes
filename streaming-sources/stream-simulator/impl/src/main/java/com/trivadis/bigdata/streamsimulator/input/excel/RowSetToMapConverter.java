package com.trivadis.bigdata.streamsimulator.input.excel;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameAwareConverter;
import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;
import com.trivadis.bigdata.streamsimulator.input.StringArrayToMapConverter;

/**
 * {@link Converter} to convert a {@link RowSet} into a (column name, record value) map.<br>
 * The column names are provided by the given {@link ColumnNameProvider}.
 *
 * @author Markus Zehnder
 */
public class RowSetToMapConverter extends ColumnNameAwareConverter<RowSet, Map<String, String>> {

    @Override
    public Map<String, String> convert(RowSet rs) {
        return new StringArrayToMapConverter(columnNameProvider).convert(rs.getCurrentRow());
    }

}
