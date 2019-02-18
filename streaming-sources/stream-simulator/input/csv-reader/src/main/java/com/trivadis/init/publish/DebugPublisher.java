package com.trivadis.init.publish;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class DebugPublisher implements EventPublisher {

	public void send(String channelName, String message) {

		System.out.println("Publish to channel " + channelName + ": " + message);
	}

}
