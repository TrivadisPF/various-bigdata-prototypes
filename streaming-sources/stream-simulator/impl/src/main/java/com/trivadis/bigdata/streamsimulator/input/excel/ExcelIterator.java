package com.trivadis.bigdata.streamsimulator.input.excel;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Excel row iterator. It will return rows from the Excel file from all sheets, starting at the first one.
 * 
 * @author Markus Zehnder
 *
 * @param <T> the type of the row returned by the given {@link ExcelReader}
 */
public class ExcelIterator<T> implements Iterator<T> {
    private final ExcelReader<T> reader;

    private T nextLine;

    public ExcelIterator(ExcelReader<T> reader) throws IOException {
        this.reader = reader;
        nextLine = reader.readNext();
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public T next() {
        if (nextLine == null) {
            throw new NoSuchElementException("No more records");
        }
        T temp = nextLine;
        try {
            nextLine = reader.readNext();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return temp;
    }

}
