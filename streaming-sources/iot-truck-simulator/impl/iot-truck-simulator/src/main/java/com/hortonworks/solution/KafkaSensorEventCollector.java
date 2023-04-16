package com.hortonworks.solution;

import java.util.Properties;
import java.util.concurrent.Future;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.*;
import org.apache.log4j.Logger;

import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;
import org.codehaus.plexus.util.StringUtils;

public class KafkaSensorEventCollector extends AbstractSensorEventCollector {

	static final String TOPIC_TRUCK_POSITION = StringUtils.split(Lab.topicName, ",")[0];
	static final String TOPIC_TRUCK_DRIVING_INFO = StringUtils.split(Lab.topicName, ",")[1];
	private Logger logger = Logger.getLogger(this.getClass());

	Producer<String, String> producer = null;
	Producer<String, SpecificRecord> producerAvro = null;

	private void connect() {
		Properties props = new Properties();
		props.put("bootstrap.servers", Lab.host + ":" + Lab.port);
		if (!StringUtils.isWhitespace(Lab.securityProtocol))
			props.put("security.protocol", Lab.securityProtocol);
		if (!StringUtils.isWhitespace(Lab.saslMechanism))
			props.put("sasl.mechanism", Lab.saslMechanism);
		if (!StringUtils.isWhitespace(Lab.saslUsername) && !StringUtils.isWhitespace(Lab.saslPassword))
			props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + Lab.saslUsername + "\" password=\"" + Lab.saslPassword + "\";");
		props.put("acks", "all");
		props.put("retries", 0);
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		if (Lab.format.equals(Lab.AVRO)) {
			if (Lab.schemaRegistryKind.equals(Lab.CONFLUENT)) {
				props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
				props.put("schema.registry.url", Lab.schemaRegistryUrl);
				props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, true);
			} else if (Lab.schemaRegistryKind.equals(Lab.AZURE)) {
				TokenCredential credential;
				credential = new ClientSecretCredentialBuilder()
						.tenantId(Lab.azureTenantId)
						.clientId(Lab.azureClientId)
						.clientSecret(Lab.azureClientSecret)
						.build();
				props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializer.class);
				props.put("schema.registry.url", Lab.schemaRegistryUrl);
				props.put(com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig.SCHEMA_REGISTRY_CREDENTIAL_CONFIG, credential);
				props.put(com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS_CONFIG, true);
				props.put(com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig.SCHEMA_GROUP_CONFIG, Lab.azureSchemaGroup);
			}
		} else {
	    	props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		}

		try {
			if (Lab.format.equals(Lab.AVRO)) {
				producerAvro = new KafkaProducer<String, SpecificRecord>(props);
			} else {
				producer = new KafkaProducer<String, String>(props);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public KafkaSensorEventCollector() {

		if (producer == null || producerAvro == null) {
			connect();
		}

	}

	@Override
	protected String getTopicName(Integer eventKind, MobileEyeEvent originalEvent) {
		String topicName = null;
		if (eventKind.equals(MobileEyeEvent.EVENT_KIND_BEHAVIOUR_AND_POSITION)) {
			topicName = TOPIC_TRUCK_POSITION;
		} else if (eventKind.equals(MobileEyeEvent.EVENT_KIND_POSITION)) {
			topicName = TOPIC_TRUCK_POSITION;
		} else if (eventKind.equals(MobileEyeEvent.EVENT_KIND_BEHAVIOUR)) {
			topicName = TOPIC_TRUCK_DRIVING_INFO;
		}
		return topicName;	
	}

	@Override
	protected void sendMessage(String topicName, MobileEyeEvent originalEvent, Object message) {
		if (Lab.vehicleFilters != null && Lab.vehicleFilters.contains(originalEvent.getTruck().getTruckId())
				|| Lab.vehicleFilters == null) {
			String truckId = String.valueOf(originalEvent.getTruck().getTruckId());

			ProducerRecord<String, SpecificRecord> recordAvro = null;
			ProducerRecord<String, String> record = null;

			if (Lab.format.equals(Lab.AVRO)) {
				recordAvro = new ProducerRecord<String, SpecificRecord>(topicName, truckId, (SpecificRecord)message);
			} else {
				record = new ProducerRecord<String, String>(topicName, truckId, (String) message);
			}
			if (producer != null || producerAvro != null) {
				try {
					Future<RecordMetadata> future = null;
					if (Lab.format.equals(Lab.AVRO)) {
						future = producerAvro.send(recordAvro);
					} else {
						future = producer.send(record);
					}
					RecordMetadata metadata = future.get();
				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
