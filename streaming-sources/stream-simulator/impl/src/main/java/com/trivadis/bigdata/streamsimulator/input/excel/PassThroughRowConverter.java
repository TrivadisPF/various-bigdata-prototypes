package com.trivadis.bigdata.streamsimulator.input.excel;

import org.springframework.core.convert.converter.Converter;

/**
 * Pass through Excel row {@link Converter} for returning the original String array result of the current record without
 * conversion.
 *
 * @author Markus Zehnder
 */
public class PassThroughRowConverter implements Converter<RowSet, String[]> {

    @Override
    public String[] convert(RowSet rs) {
        return rs.getCurrentRow();
    }

}
