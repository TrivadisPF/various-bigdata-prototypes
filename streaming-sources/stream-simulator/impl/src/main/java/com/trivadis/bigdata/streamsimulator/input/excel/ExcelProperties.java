package com.trivadis.bigdata.streamsimulator.input.excel;

/**
 * Excel configuration.
 * 
 * @author Markus Zehnder
 */
public class ExcelProperties {
    private int skipLines = 0;
    private boolean skipEmptyLines = true;
    private boolean firstLineIsHeader = true;
    private String[] staticHeader = new String[0];
    private int startIndex = 0;

    public int getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
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
