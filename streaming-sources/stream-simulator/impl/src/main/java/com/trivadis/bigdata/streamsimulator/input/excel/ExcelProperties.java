package com.trivadis.bigdata.streamsimulator.input.excel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Excel configuration.
 * 
 * @author Markus Zehnder
 */
@Getter
@Setter
@ToString
public class ExcelProperties {
    private int skipLines = 0;
    private boolean skipEmptyLines = true;
    private boolean firstLineIsHeader = true;
    private String[] staticHeader = new String[0];
    private int startIndex = 0;
}
