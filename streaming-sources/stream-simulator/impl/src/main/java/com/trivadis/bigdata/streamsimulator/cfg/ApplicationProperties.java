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
import com.trivadis.bigdata.streamsimulator.input.excel.ExcelProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Application specific configuration properties
 * 
 * @author Markus Zehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator")
@Getter
@Setter
public class ApplicationProperties {
    private File inputDirectory;
    private LocalDateTime referenceDate;
    private AdjustDates adjustDates;
    private Speedup speedup;
    private Throttling throttling;
    private Source source;

    @Getter
    @Setter
    public static class AdjustDates {
        private boolean enabled = false;
        private String dateFieldNameRegex = "timestamp";
        private String dateFieldPattern;
    }

    @Getter
    @Setter
    public static class Speedup {
        private boolean enabled = false;
        private float factor = 1f;
        private boolean simpleMode = true;
        private String referenceFieldName;
        private String referenceFieldNamePattern;
        private int maxDelayedMessages = 1000;
    }

    @Getter
    @Setter
    public static class Throttling {
        private boolean enabled = false;
        private long fixedDelay = 1000;
        private int maxMessagesPerPoll = 1;
    }

    @Getter
    @Setter
    public static class Source {
        private CsvProperties csv = new CsvProperties();
        private ExcelProperties excel = new ExcelProperties();
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
