package com.trivadis.bigdata.streamsimulator.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageHandler;

/**
 * MQTT specific configuration. Activated by application property
 * simulater.output=mqtt
 * 
 * @author mzehnder
 */
@Configuration
@ConditionalOnProperty(name = "simulator.output", havingValue = "mqtt")
public class MqttConfig {

    @Autowired
    MqttProperties cfg;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(cfg.getMqttConnectOptions());
        return factory;
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(cfg.getClientName(), mqttClientFactory());
        messageHandler.setAsync(cfg.isAsync());
        messageHandler.setDefaultTopic(cfg.getDefaultTopic());
        return messageHandler;
    }

    @Bean
    public IntegrationFlow mqttFlow() {
        // TODO move hard coded channel name to application configuration
        return IntegrationFlows.from("inboundChannel")
                .transform(new ObjectToJsonTransformer())
          .handle(mqttOutbound())
          .get();
    }
}
