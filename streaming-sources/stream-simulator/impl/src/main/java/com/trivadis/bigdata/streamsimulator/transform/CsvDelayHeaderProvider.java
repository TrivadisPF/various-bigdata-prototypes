package com.trivadis.bigdata.streamsimulator.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;
import com.trivadis.bigdata.streamsimulator.msg.MsgHeader;

/**
 * Calculate the delay from a CSV message.
 * 
 * @author mzehnder
 */
public class CsvDelayHeaderProvider implements HeaderProvider<Map<String, String>> {

    private final String eventTimeFieldName;
    private final DateTimeFormatter eventTimeFormatter;
    private final long referenceTimeStamp;
    private final float timeFactor;

    public CsvDelayHeaderProvider(long referenceTimestamp, Speedup speedup) {
        this.eventTimeFieldName = speedup.getReferenceFieldName();
        this.eventTimeFormatter = DateTimeFormatter.ofPattern(speedup.getReferenceFieldNamePattern());
        this.referenceTimeStamp = referenceTimestamp;
        this.timeFactor = speedup.getFactor();
    }

    @Override
    public Map<String, ?> getHeaders(Map<String, String> payload) {
        LocalDateTime date = LocalDateTime.parse(payload.get(eventTimeFieldName), eventTimeFormatter);
        long timestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long delay = (long) ((timestamp - referenceTimeStamp) / timeFactor);

        return Collections.singletonMap(MsgHeader.DELAY, new Date(System.currentTimeMillis() + delay));
    }

}
