package com.trivadis.bigdata.streamsimulator.input.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * CSV configuration.
 * 
 * @author mzehnder
 */
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

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        if (separator.length() == 1) {
            this.separator = separator.charAt(0);
        }
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        if (quoteChar.length() == 1) {
            this.quoteChar = quoteChar.charAt(0);
        }
    }

    public char getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        if (escapeChar.length() == 1) {
            this.escapeChar = escapeChar.charAt(0);
        }
    }

    public int getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

    public boolean isIgnoreLeadingWhiteSpace() {
        return ignoreLeadingWhiteSpace;
    }

    public void setIgnoreLeadingWhiteSpace(boolean ignoreLeadingWhiteSpace) {
        this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
    }

    public boolean isIgnoreQuotations() {
        return ignoreQuotations;
    }

    public void setIgnoreQuotations(boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
    }

    public boolean isSkipEmptyLines() {
        return skipEmptyLines;
    }

    public void setSkipEmptyLines(boolean skipEmptyLines) {
        this.skipEmptyLines = skipEmptyLines;
    }

    public boolean isFirstLineIsHeader() {
        return firstLineIsHeader;
    }

    public void setFirstLineIsHeader(boolean firstLineIsHeader) {
        this.firstLineIsHeader = firstLineIsHeader;
    }

    public String[] getStaticHeader() {
        return staticHeader;
    }

    public void setStaticHeader(String[] staticHeader) {
        this.staticHeader = staticHeader;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

}
