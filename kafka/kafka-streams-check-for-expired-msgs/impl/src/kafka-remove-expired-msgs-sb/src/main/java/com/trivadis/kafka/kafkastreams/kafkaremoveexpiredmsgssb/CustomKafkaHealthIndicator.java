package com.trivadis.kafka.kafkastreams.kafkaremoveexpiredmsgssb;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class CustomKafkaHealthIndicator extends AbstractHealthIndicator {
	
	@Autowired
	private StreamsBuilderFactoryBean myKStreamBuilderFactoryBean;
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		builder.up()
			.withDetail("status", myKStreamBuilderFactoryBean.getKafkaStreams().state().toString())
        	.withDetail("app", "Alive and Kicking")
        	.withDetail("error", "Nothing! I'm good.");		
	}

	
}
