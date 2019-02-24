package com.trivadis.bigdata.streamsimulator.transform;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.AdjustDates;

/**
 * Adjust date values in a map by a given date and time offset.
 * 
 * @author Markus Zehnder
 */
public class TransformDates {
    private final Pattern fieldNamePattern;
    private final Duration adjustDuration;
    private final DateTimeFormatter formatter;

    public TransformDates(Duration adjustDuration, AdjustDates adjustment) {
        this.adjustDuration = adjustDuration;
        this.fieldNamePattern = Pattern.compile(adjustment.getDateFieldNameRegex());
        this.formatter = DateTimeFormatter.ofPattern(adjustment.getDateFieldPattern());
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
                dataMap.put(key, date.plus(adjustDuration).format(formatter));
            }
        }
        return dataMap;
    }

}
