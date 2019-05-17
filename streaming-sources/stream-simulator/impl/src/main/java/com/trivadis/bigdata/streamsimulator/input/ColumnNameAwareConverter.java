package com.trivadis.bigdata.streamsimulator.input;

import org.springframework.core.convert.converter.Converter;

public abstract class ColumnNameAwareConverter<S, T> implements Converter<S, T> {
    protected ColumnNameProvider<?> columnNameProvider;

    public void setColumnNameProvider(ColumnNameProvider<S> columnNameProvider) {
        this.columnNameProvider = columnNameProvider;
    }

}
