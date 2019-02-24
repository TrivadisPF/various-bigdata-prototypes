package com.trivadis.bigdata.streamsimulator.input;


/**
 * Header column name provider for a given input type.
 * 
 * @author Markus Zehnder
 *
 * @param <T> Input object type, e.g. an Excel sheet or CSV file
 */
public interface ColumnNameProvider<T> {

    /**
     * Initialize the provider from the given source
     * 
     * @param source the related source of the column names
     */
    default void init(T source) {};
    
    /**
     * Retrieves the column names in the given Type.
     *
     * @param source the source object
     * @return the column names
     */
    String[] getColumnNames();

}
