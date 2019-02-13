package com.trivadis.bigdata.streamsimulator.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.JmsSendingMessageHandler;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHandler;

/**
 * ActiveMQ specific configuration. Activated by application property
 * simulater.output=activemq
 * 
 * @author mzehnder
 */
@Configuration
@ConditionalOnProperty(name = "simulator.output", havingValue = "activemq")
public class ActiveMqConfig {

    @Autowired
    private JmsTemplate jmsTemplate;

    public MessageHandler jmsOutbound() {
        JmsSendingMessageHandler messageHandler = new JmsSendingMessageHandler(jmsTemplate);
        return messageHandler;
    }

    @Bean
    public IntegrationFlow activeMqFlow() {
        // TODO move hard coded channel name to application configuration
        return IntegrationFlows.from("inboundChannel")
                .transform(new ObjectToJsonTransformer())
                .handle(jmsOutbound())
                .get();
    }
}
