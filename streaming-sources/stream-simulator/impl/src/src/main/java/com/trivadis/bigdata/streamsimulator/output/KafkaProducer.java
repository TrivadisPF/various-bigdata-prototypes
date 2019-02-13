package com.trivadis.bigdata.streamsimulator.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * Kafak message handler
 * 
 * @author mzehnder
 */
public class KafkaProducer implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private long sendCount;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        logger.debug("Producing message: {}", message);

        kafkaTemplate.send(message);
        sendCount++;

        if (sendCount % 1000 == 0) {
            logger.info("Published {} messages", sendCount);
        }

    }

}
