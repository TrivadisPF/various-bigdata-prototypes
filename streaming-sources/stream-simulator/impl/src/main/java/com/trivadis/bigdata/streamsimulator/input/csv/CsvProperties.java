package com.trivadis.bigdata.streamsimulator.input.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * CSV configuration.
 * 
 * @author Markus Zehnder
 */
@Getter
@Setter
@ToString
public  class CsvProperties {
    private Charset charset = StandardCharsets.UTF_8;
    private char separator = ',';
    private char quoteChar = '"';
    private char escapeChar = '\\';
    private int skipLines = 0;
    private boolean skipEmptyLines = true;
    private boolean firstLineIsHeader = true;
    private String[] staticHeader = new String[0];
    private int startIndex = 0;
    private boolean ignoreLeadingWhiteSpace = false;
    private boolean ignoreQuotations = false;
}
