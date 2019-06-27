package com.trivadis.bigdata.streamsimulator.output;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import lombok.extern.slf4j.Slf4j;

/**
 * Kafak message handler
 * 
 * @author Markus Zehnder
 */
@Slf4j
public class KafkaProducer implements MessageHandler {

    private long sendCount;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        log.debug("Producing message: {}", message);

        kafkaTemplate.send(message);
        sendCount++;

        if (sendCount % 1000 == 0) {
            log.info("Published {} messages", sendCount);
        }

    }

}
