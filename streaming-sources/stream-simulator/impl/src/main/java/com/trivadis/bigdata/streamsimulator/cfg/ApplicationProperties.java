package com.trivadis.bigdata.streamsimulator.cfg;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;

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
    private LocalDateTime referenceDate;
    private AdjustDates adjustDates;
    private Speedup speedup;
    private Throttling throttling;
    private Source source;

    public static class AdjustDates {
        private boolean enabled = false;
        private String dateFieldNameRegex = "timestamp";
        private String dateFieldPattern;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDateFieldNameRegex() {
            return dateFieldNameRegex;
        }

        public void setDateFieldNameRegex(String dateFieldNameRegex) {
            this.dateFieldNameRegex = dateFieldNameRegex;
        }

        public String getDateFieldPattern() {
            return dateFieldPattern;
        }

        public void setDateFieldPattern(String dateFieldPattern) {
            this.dateFieldPattern = dateFieldPattern;
        }
    }

    public static class Speedup {
        private boolean enabled = false;
        private float factor = 1f;
        private boolean simpleMode = true;
        private String referenceFieldName;
        private String referenceFieldNamePattern;
        private int maxDelayedMessages = 1000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public float getFactor() {
            return factor;
        }

        public void setFactor(float factor) {
            this.factor = factor;
        }

        public boolean isSimpleMode() {
            return simpleMode;
        }

        public void setSimpleMode(boolean simpleMode) {
            this.simpleMode = simpleMode;
        }

        public String getReferenceFieldName() {
            return referenceFieldName;
        }

        public void setReferenceFieldName(String referenceFieldName) {
            this.referenceFieldName = referenceFieldName;
        }

        public String getReferenceFieldNamePattern() {
            return referenceFieldNamePattern;
        }

        public void setReferenceFieldNamePattern(String referenceFieldNamePattern) {
            this.referenceFieldNamePattern = referenceFieldNamePattern;
        }

        public int getMaxDelayedMessages() {
            return maxDelayedMessages;
        }

        public void setMaxDelayedMessages(int count) {
            this.maxDelayedMessages = count;
        }
    }

    public static class Throttling {
        private boolean enabled = false;
        private long fixedDelay = 1000;
        private int maxMessagesPerPoll = 1;

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

        public int getMaxMessagesPerPoll() {
            return maxMessagesPerPoll;
        }

        public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
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

    public AdjustDates getAdjustDates() {
        return adjustDates;
    }

    public void setAdjustDates(AdjustDates adjustDates) {
        this.adjustDates = adjustDates;
    }

    public Speedup getSpeedup() {
        return speedup;
    }

    public void setSpeedup(Speedup speedup) {
        this.speedup = speedup;
    }

    public Throttling getThrottling() {
        return throttling;
    }

    public void setThrottling(Throttling throttling) {
        this.throttling = throttling;
    }

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        if (referenceDate.contains("T")) {
            this.referenceDate = LocalDateTime.parse(referenceDate);
        } else {
            this.referenceDate = LocalDateTime.of(LocalDate.parse(referenceDate), LocalTime.MIDNIGHT);
        }
    }

    public long getReferenceTimestamp() {
        return referenceDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public Period getAdjustPeriod() {
        return Period.between(LocalDate.from(getReferenceDate()), LocalDate.now());
    }

    public Duration getAdjustDuration() {
        return Duration.between(getReferenceDate(), LocalDateTime.now());
    }

}
