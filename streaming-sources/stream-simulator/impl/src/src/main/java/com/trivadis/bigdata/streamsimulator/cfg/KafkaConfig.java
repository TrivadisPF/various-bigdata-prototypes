package com.trivadis.bigdata.streamsimulator.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.MessageChannel;

import com.trivadis.bigdata.streamsimulator.output.KafkaProducer;

/**
 * Kafka specific configuration. Activated by application property
 * simulater.output=kafka
 * 
 * @author mzehnder
 */
@Configuration
@ConditionalOnProperty(name = "simulator.output", havingValue = "kafka")
public class KafkaConfig {

    @Autowired
    ApplicationProperties cfg;

    @Bean
    public KafkaProducer kafkaProducer() {
        KafkaProducer producer = new KafkaProducer();
        return producer;
    }
 
    @Bean
    public IntegrationFlow kafkaFlow(MessageChannel inboundChannel) {
        return addThrottling(IntegrationFlows.from(inboundChannel))
            .handle(kafkaProducer())
            .get();
    }

    // quick and dirty throttling test
    IntegrationFlowBuilder addThrottling(IntegrationFlowBuilder builder) {
        return cfg.isThrottling() ? builder
                .channel(new QueueChannel(100))
                .bridge(e -> e.poller(Pollers.fixedDelay(1000).maxMessagesPerPoll(10))) 
                : builder;
    }
}
