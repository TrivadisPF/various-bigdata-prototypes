/*
 * Copyright 2006-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trivadis.bigdata.streamsimulator.input.excel;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import com.trivadis.bigdata.streamsimulator.input.ColumnNameProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * Excel file reader using Apache POI. It will read the Excel file sheet for sheet and row for row.
 * 
 * @param <T> the type of the converted row returned by the given row {@link Converter}.
 * 
 * @author Marten Deinum (original Spring Batch AbstractExcelItemReader / PoiItemReader)
 * @author Markus Zehnder
 *
 * @see <a href="https://github.com/mdeinum/spring-batch-extensions">Inspired by Spring Batch Extension fork by
 *      mdeinum</a>
 */
@Slf4j
public class ExcelReader<T> implements Closeable, Iterable<T> {

    private final File file;
    private Workbook workbook;

    private int linesToSkip = 0;
    private int currentSheet = 0;
    private int endAfterBlankLines = 1;
    private boolean noInput = true;
    private final Converter<RowSet, T> rowConverter;
    private final ColumnNameProvider<RowSet> columnNameProvider;
    private RowSet rs;

    public ExcelReader(File file, Converter<RowSet, T> rowMapper, ColumnNameProvider<RowSet> columnNameProvider)
            throws IOException {
        this.file = file;
        this.rowConverter = rowMapper;
        this.columnNameProvider = columnNameProvider;
    }

    public void open() throws IOException {
        Assert.notNull(file, "Input file must be set");
        noInput = true;
        if (!file.exists()) {
            throw new IllegalStateException("Input file must exist: " + file);
        }

        if (!file.canRead()) {
            throw new IllegalStateException("Input resource must be readable: " + file);
        }

        workbook = WorkbookFactory.create(file);
        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        openSheet();
        noInput = false;
        if (log.isDebugEnabled()) {
            log.debug("Opened workbook [{}] with {} sheets.", file.getName(), workbook.getNumberOfSheets());
        }
    }

    /**
     * Skip a given number of lines.
     * 
     * @param numberOfLinesToSkip The number of lines to skip
     * @throws IOException If anything bad happens when reading the file
     */
    public void skip(int numberOfLinesToSkip) throws IOException {
        for (int j = 0; j < numberOfLinesToSkip; j++) {
            readNext();
        }
    }

    public T readNext() throws IOException {
        T item = doRead();
        int blankLines = 0;
        while (item == null) {
            blankLines++;
            if (blankLines >= endAfterBlankLines) {
                return null;
            }
            item = doRead();
            if (item != null) {
                return item;
            }
        }
        return item;
    }

    protected T doRead() throws IOException {
        if (noInput || rs == null) {
            return null;
        }

        if (rs.next()) {
            // skip all the blank row from which content has been deleted but still a valid row
            while (null != rs.getCurrentRow() && isInvalidValidRow(rs)) {
                rs.next();
            }
            try {
                return rowConverter.convert(rs);
            } catch (final Exception e) {
                throw new ExcelFileParseException("Exception parsing Excel file.", e, file.getName(),
                        rs.getSheetName(), rs.getCurrentRowIndex(), rs.getCurrentRow());
            }
        } else {
            currentSheet++;
            if (currentSheet >= workbook.getNumberOfSheets()) {
                if (log.isDebugEnabled()) {
                    log.debug("No more sheets in '{}'", file.getName());
                }
                return null;
            } else {
                openSheet();
                return doRead();
            }
        }

    }

    @Override
    public Iterator<T> iterator() {
        try {
            return new ExcelIterator<T>(this);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (workbook != null) {
            noInput = true;
            currentSheet = 0;
            rs = null;
            workbook.close();
            workbook = null;
        }
    }

    private void openSheet() {
        final Sheet sheet = workbook.getSheetAt(currentSheet);
        rs = new RowSet(sheet);
        columnNameProvider.init(rs);

        if (log.isDebugEnabled()) {
            log.debug("Opening sheet {}", sheet.getSheetName());
        }

        for (int i = 0; i < linesToSkip; i++) {
            rs.next();
        }
        if (log.isDebugEnabled()) {
            log.debug("Openend sheet {} with {} rows.", sheet.getSheetName(), rs.getNumberOfRows());
        }
    }

    private boolean isInvalidValidRow(RowSet rs) {
        for (String str : rs.getCurrentRow()) {
            if (str.length() > 0) {
                return false;
            }
        }
        return true;
    }

}
