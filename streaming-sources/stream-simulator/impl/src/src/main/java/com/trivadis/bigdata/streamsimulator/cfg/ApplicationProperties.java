package com.trivadis.bigdata.streamsimulator.cfg;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application specific configuration properties
 * 
 * @author mzehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator")
public class ApplicationProperties {
    private Source source;

    public static class Source {
        private Csv csv;

        public Csv getCsv() {
            return csv;
        }

        public void setCsv(Csv csv) {
            this.csv = csv;
        }
    }

    public static class Csv {
        private Charset charset = Charset.forName("UTF-8");
        private char separator = ',';
        private char quoteChar = '"';
        private char escapeChar = '\\';
        private int skipLines = 0;
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

    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

}
