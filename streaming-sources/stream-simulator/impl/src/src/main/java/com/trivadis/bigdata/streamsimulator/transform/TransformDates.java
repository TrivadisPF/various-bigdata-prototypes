package com.trivadis.bigdata.streamsimulator.transform;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adjust date values in a map by a given date and time offset.
 * 
 * @author mzehnder
 */
public class TransformDates {
    private final Pattern fieldNamePattern;
    private final Period adjustPeriod;
    private final Duration adjustDuration;
    private final DateTimeFormatter formatter;

    public TransformDates(Period adjustPeriod, Duration adjustDuration, String fieldNameRegex,
            DateTimeFormatter formatter) {
        this.adjustPeriod = adjustPeriod;
        this.adjustDuration = adjustDuration;
        this.fieldNamePattern = Pattern.compile(fieldNameRegex);
        this.formatter = formatter;
    }

    /**
     * Transforms all date fields which match the configured field name pattern. The date values are adjusted with the
     * configured time period and duration.
     * 
     * @param dataMap the map to adjust
     * @return the adjusted map
     * @throws ParseException Thrown if a date value doesn't match the date time formatter.
     */
    public Map<String, String> transform(Map<String, String> dataMap) throws ParseException {
        Matcher m = fieldNamePattern.matcher("");
        for (String key : dataMap.keySet()) {
            if (m.reset(key).matches()) {
                LocalDateTime date = LocalDateTime.parse(dataMap.get(key), formatter);
                dataMap.put(key, date.plus(adjustPeriod).plus(adjustDuration).format(formatter));
            }
        }
        return dataMap;
    }

}
