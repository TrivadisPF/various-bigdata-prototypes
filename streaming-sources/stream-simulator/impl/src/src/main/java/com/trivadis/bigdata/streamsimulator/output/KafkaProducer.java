package com.trivadis.bigdata.streamsimulator.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * TODO make configurable, most likely through a bean factory to select
 * configured output producer.
 * 
 * @author mzehnder
 */
@Service
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String TOPIC = "first_topic";

    private long sendCount;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * FIXME use generic message object to introduce other message types TODO
     * optional key parameter
     * 
     * @param message Message to publish
     */
    public void sendMessage(String message) {
        logger.debug("Producing message: {}", message);

        kafkaTemplate.send(TOPIC, message);
        sendCount++;

        if (sendCount % 1000 == 0) {
            logger.info("Published {} messages", sendCount);
        }
    }
}