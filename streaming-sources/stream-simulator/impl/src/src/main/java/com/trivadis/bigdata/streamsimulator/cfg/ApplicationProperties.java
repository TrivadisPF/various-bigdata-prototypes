package com.trivadis.bigdata.streamsimulator.cfg;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.trivadis.bigdata.streamsimulator.input.csv.CsvProperties;

/**
 * Application specific configuration properties
 * 
 * @author mzehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator")
public class ApplicationProperties {
    private File inputDirectory;
    private Throttling throttling;
    private Source source;

    public static class Throttling {
        private boolean enabled = false;
        private long fixedDelay = 1000;
        private long maxMessagesPerPoll = 1;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getFixedDelay() {
            return fixedDelay;
        }

        public void setFixedDelay(long fixedDelay) {
            this.fixedDelay = fixedDelay;
        }

        public long getMaxMessagesPerPoll() {
            return maxMessagesPerPoll;
        }

        public void setMaxMessagesPerPoll(long maxMessagesPerPoll) {
            this.maxMessagesPerPoll = maxMessagesPerPoll;
        }
    }

    public static class Source {
        private CsvProperties csv;

        public CsvProperties getCsv() {
            return csv;
        }

        public void setCsv(CsvProperties csv) {
            this.csv = csv;
        }
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public Throttling getThrottling() {
        return throttling;
    }

    public void setThrottling(Throttling throttling) {
        this.throttling = throttling;
    }

}
