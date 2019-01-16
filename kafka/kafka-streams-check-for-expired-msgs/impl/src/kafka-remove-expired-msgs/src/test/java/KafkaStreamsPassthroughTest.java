import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.Before;
import org.junit.Test;

import com.trivadis.kafkastreams.KafkaStreamsPassthroughGenericAvro;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerializer;

public class KafkaStreamsPassthroughTest {
	TopologyTestDriver testDriver = null;
	
	@Before
	public void setUp() {
		
		KafkaStreamsPassthroughGenericAvro sut = new KafkaStreamsPassthroughGenericAvro(); 
		
		// setup test driver
		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
		//config.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
		
		testDriver = new TopologyTestDriver(sut.getTopology("sourceTopic", "targetTopic", "http://localhost:8081/", true), config);
	}
	
	@Test
	public void test1() throws IOException {
		Schema schema = new Schema.Parser().parse(new File("/mnt/hgfs/git/gschmutz/various-research/kafka-streams-removestale/src/kafka-remove-old/src/test/java/message.avsc"));

		GenericRecord testMessage = new GenericData.Record(schema);
		testMessage.put("value", "abc");
		
		Serde<GenericRecord> keyGenericAvroSerde = new GenericAvroSerde();
		boolean isKeySerde = true;
		keyGenericAvroSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081/"), isKeySerde);

		Serde<GenericRecord> genericAvroSerde = new GenericAvroSerde();
		isKeySerde = false;
		genericAvroSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081/"), isKeySerde);
		
		ConsumerRecordFactory<GenericRecord, GenericRecord> factory = new ConsumerRecordFactory<GenericRecord,GenericRecord>("sourceTopic", keyGenericAvroSerde.serializer(), genericAvroSerde.serializer());
		testDriver.pipeInput(factory.create(testMessage, testMessage));
		ProducerRecord<GenericRecord,GenericRecord> record = testDriver.readOutput("targetTopic", keyGenericAvroSerde.deserializer(), genericAvroSerde.deserializer());
		
	}

}
