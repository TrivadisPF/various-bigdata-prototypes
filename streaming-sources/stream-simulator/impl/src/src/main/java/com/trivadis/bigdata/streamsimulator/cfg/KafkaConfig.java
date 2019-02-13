package com.trivadis.bigdata.streamsimulator.cfg;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

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

    @Bean
    public KafkaProducer kafkaProducer() {
        KafkaProducer producer = new KafkaProducer();
        return producer;
    }
    
    @Bean
    public IntegrationFlow kafkaFlow() {
        // TODO move hard coded channel name to application configuration
        return IntegrationFlows.from("inboundChannel")
          .handle(kafkaProducer())
          .get();
    }

}
