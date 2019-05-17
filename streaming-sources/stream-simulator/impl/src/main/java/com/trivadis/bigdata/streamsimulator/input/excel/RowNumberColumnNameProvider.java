package com.trivadis.bigdata.streamsimulator.input.excel;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * {@code ColumnNameProvider} implementation which returns the column names from the given row number in a
 * {@link RowSet}.
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class RowNumberColumnNameProvider implements ColumnNameProvider<RowSet> {

    private int headerRowNumber = 0;
    private String[] columnNames;

    public RowNumberColumnNameProvider() {
    }

    public RowNumberColumnNameProvider(int headerRowNumber) {
        this.headerRowNumber = headerRowNumber;
    }

    @Override
    public void init(RowSet rs) {
        while (rs.getCurrentRowIndex() < headerRowNumber) {
            rs.next();
        }
        columnNames = rs.getCurrentRow();

        log.debug("Initialized sheet {} with column names: {}", rs.getSheetName(), (Object) columnNames);
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    public void setHeaderRowNumber(int headerRowNumber) {
        this.headerRowNumber = headerRowNumber;
    }

}
