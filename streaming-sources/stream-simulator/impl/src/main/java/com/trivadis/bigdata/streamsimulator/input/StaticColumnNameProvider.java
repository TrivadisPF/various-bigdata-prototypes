package com.trivadis.bigdata.streamsimulator.input;

/**
 * {@code ColumnNameProvider} implementation which returns a static column name array.
 * 
 * @author Markus Zehnder
 */
public class StaticColumnNameProvider<T> implements ColumnNameProvider<T> {

    private final String[] columnNames;

    public StaticColumnNameProvider(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

}
