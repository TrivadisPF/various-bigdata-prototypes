package com.trivadis.bigdata.streamsimulator.input.excel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


/**
 * @author Marten Deinum
 * @author Markus Zehnder
 *
 * @see <a href="https://github.com/mdeinum/spring-batch-extensions">Inspired by Spring Batch Extension fork by
 *      mdeinum</a>
 */
public class RowSet {
    private final Sheet sheet;
    private final int numberOfRows;

    private int currentRowIndex = -1;
    private String[] currentRow;
    private FormulaEvaluator evaluator;

    public RowSet(Sheet sheet) {
        this.sheet = sheet;
        this.numberOfRows = sheet.getLastRowNum() + 1;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public boolean next() {
        currentRow = null;
        currentRowIndex++;
        if (currentRowIndex < numberOfRows) {
            currentRow = getRow(currentRowIndex);
            return true;
        }
        return false;
    }

    public int getCurrentRowIndex() {
        return this.currentRowIndex;
    }

    public String[] getCurrentRow() {
        return this.currentRow;
    }

    public String getColumnValue(int idx) {
        return currentRow[idx];
    }

    public String getSheetName() {
        return sheet.getSheetName();
    }

    public String[] getRow(final int rowNumber) {
        final Row row = sheet.getRow(rowNumber);
        if (row == null) {
            return null;
        }
        final List<String> cells = new LinkedList<String>();
        final int numberOfColumns = row.getLastCellNum();

        for (int i = 0; i < numberOfColumns; i++) {
            Cell cell = row.getCell(i);
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        cells.add(String.valueOf(date.getTime()));
                    } else {
                        cells.add(String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    cells.add(String.valueOf(cell.getBooleanCellValue()));
                    break;
                case STRING:
                case BLANK:
                    cells.add(cell.getStringCellValue());
                    break;
                case FORMULA:
                    cells.add(getFormulaEvaluator().evaluate(cell).formatAsString());
                    break;
                default:
                    throw new IllegalArgumentException("Cannot handle cells of type " + cell.getCellType());
            }
        }
        return cells.toArray(new String[cells.size()]);
    }

    private FormulaEvaluator getFormulaEvaluator() {
        if (this.evaluator == null) {
            this.evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        }
        return this.evaluator;
    }


}
