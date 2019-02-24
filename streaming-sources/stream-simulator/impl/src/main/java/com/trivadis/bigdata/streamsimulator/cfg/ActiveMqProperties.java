package com.trivadis.bigdata.streamsimulator.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ActiveMQ specific configuration properties
 * 
 * @author Markus Zehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator.activemq.producer")
public class ActiveMqProperties {

    // no custom properties yet - configuration handled by default Spring application properties
}
