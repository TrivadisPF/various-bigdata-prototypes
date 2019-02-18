package com.trivadis.init.publish;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaPublisher implements EventPublisher {
	private Producer<String, String> producer = null;

	private Producer<String, String> connect() {
		Producer<String, String> producer = null;

		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.69.134:9092");
//		props.put("bootstrap.servers", "192.168.69.134:9092");
		//props.put("acks", "all");
		props.put("retries", 0);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		//props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
		//props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
		
		//props.put("schema.registry.url", "http://192.168.69.136:38081");

		try {
			producer = new KafkaProducer<String, String>(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return producer;
	}

	public void send(String channelName, String message) {

		if (producer == null) {
			producer = connect();
		}

		ProducerRecord<String, String> record = new ProducerRecord<String, String>(channelName, null, message);

		if (producer != null) {
			try {
				Future<RecordMetadata> future = producer.send(record);
				RecordMetadata metadata = future.get();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

		}
	}

}
