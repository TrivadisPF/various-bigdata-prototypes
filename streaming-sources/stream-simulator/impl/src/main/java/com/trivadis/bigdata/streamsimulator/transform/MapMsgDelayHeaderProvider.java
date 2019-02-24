package com.trivadis.bigdata.streamsimulator.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;
import com.trivadis.bigdata.streamsimulator.msg.MsgHeader;

/**
 * Calculate the delay from a reference time field within a Map message.
 * 
 * @author Markus Zehnder
 */
public class MapMsgDelayHeaderProvider implements HeaderProvider<Map<String, String>> {
    private static final Logger log = LoggerFactory.getLogger(MapMsgDelayHeaderProvider.class);

    private final String eventTimeFieldName;
    private final DateTimeFormatter eventTimeFormatter;
    private final long referenceTimeStamp;
    private final float timeFactor;

    public MapMsgDelayHeaderProvider(long referenceTimestamp, Speedup speedup) {
        this.eventTimeFieldName = speedup.getReferenceFieldName();
        this.eventTimeFormatter = DateTimeFormatter.ofPattern(speedup.getReferenceFieldNamePattern());
        this.referenceTimeStamp = referenceTimestamp;
        this.timeFactor = speedup.getFactor();
    }

    @Override
    public Map<String, ?> getHeaders(Map<String, String> payload) {
        String eventTime = payload.get(eventTimeFieldName);
        if (StringUtils.isBlank(eventTime)) {
            log.warn("No eventTime found in field '{}' of message {}: message will not be delayed!", eventTimeFieldName, payload);
            return null;
        }
        LocalDateTime date = LocalDateTime.parse(eventTime, eventTimeFormatter);
        long timestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long delay = (long) ((timestamp - referenceTimeStamp) / timeFactor);

        return Collections.singletonMap(MsgHeader.DELAY, new Date(System.currentTimeMillis() + delay));
    }

}