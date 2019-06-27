package com.trivadis.bigdata.streamsimulator.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.StringUtils;

import com.trivadis.bigdata.streamsimulator.cfg.ApplicationProperties.Speedup;
import com.trivadis.bigdata.streamsimulator.msg.MsgHeader;

import lombok.extern.slf4j.Slf4j;

/**
 * Calculate the delay from a reference time field within a generic Avro message.
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class AvroGenericMsgDelayHeaderProvider implements HeaderProvider<GenericRecord> {

    private final String eventTimeFieldName;
    private final DateTimeFormatter eventTimeFormatter;
    private final long referenceTimeStamp;
    private final float timeFactor;

    public AvroGenericMsgDelayHeaderProvider(long referenceTimestamp, Speedup speedup) {
        this.eventTimeFieldName = speedup.getReferenceFieldName();
        this.eventTimeFormatter = DateTimeFormatter.ofPattern(speedup.getReferenceFieldNamePattern());
        this.referenceTimeStamp = referenceTimestamp;
        this.timeFactor = speedup.getFactor();
    }

    @Override
    public Map<String, ?> getHeaders(GenericRecord payload) {
        Object eventTime = payload.get(eventTimeFieldName);
        if (eventTime == null || StringUtils.isBlank(eventTime.toString())) {
            log.warn("No eventTime found in field '{}' of message {}: message will not be delayed!", eventTimeFieldName,
                    payload);
            return null;
        }
        LocalDateTime date = LocalDateTime.parse(eventTime.toString(), eventTimeFormatter);
        long timestamp = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long delay = (long) ((timestamp - referenceTimeStamp) / timeFactor);

        return Collections.singletonMap(MsgHeader.DELAY, new Date(System.currentTimeMillis() + delay));
    }

}