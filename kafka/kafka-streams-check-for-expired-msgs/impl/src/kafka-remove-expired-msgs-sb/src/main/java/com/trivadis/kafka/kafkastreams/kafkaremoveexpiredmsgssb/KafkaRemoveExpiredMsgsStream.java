package com.trivadis.kafka.kafkastreams.kafkaremoveexpiredmsgssb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaRemoveExpiredMsgsStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRemoveExpiredMsgsStream.class);

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${kafka.schema-registry-url}")
    private String schemaRegistryUrl;

    @Value("${kafka-streams.applicationId}")
	private String applicationId;
	
    @Value("${kafka-streams.topic.source}")
    private String sourceTopic;

    @Value("${kafka-streams.topic.sink}")
    private String targetTopic;
    
    @Value("${kafka-streams.expired-check}")
    private boolean isExpiredCheck;

    @Value("${kafka-streams.verbose}")
    private boolean isVerbose;
    
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfigs() {
    	System.out.println("=====> in kafkaStreamsConfigs");
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        
		// Where to find the Confluent schema registry instance(s)
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        
		// Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        
        //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

//    @Bean("app2StreamBuilder")
//    public StreamsBuilderFactoryBean streamBuilderFactoryBean(KafkaStreamsConfiguration streamsConfig) {
//      return new StreamsBuilderFactoryBean(streamsConfig);
//    }

    @Bean
    public KStream<?, ?> kafkaStream(StreamsBuilder kStreamBuilder) {
		StoreBuilder<KeyValueStore<GenericRecord, Long>> dedupStore = null;

		// Create a state store manually.
		final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);

        final Serde<GenericRecord> avroSerde = new GenericAvroSerde();
        avroSerde.configure(serdeConfig, false); // `false` for record values

        // if the exireCheck is enabled, the corresponding statestore needs to be created and added to the topology
		if (isExpiredCheck) {
	        dedupStore = Stores
					.keyValueStoreBuilder(Stores.persistentKeyValueStore("DedupStore"), avroSerde, Serdes.Long())
					.withCachingEnabled();

			// Important (1 of 2): You must add the state store to the topology, otherwise
			// your application
			// will fail at run-time (because the state store is referred to in
			// `transform()` below.
	        kStreamBuilder.addStateStore(dedupStore);
		}

		KStream<GenericRecord, GenericRecord> stream = kStreamBuilder.stream(sourceTopic);

		// check if it should be checked for expiration of a record against the store
		if (isExpiredCheck) {
			stream.transform(new DedupTransformerSupplier(dedupStore.name(), isVerbose)
													, dedupStore.name())
					.filter(new ValueIsNotNullPredicate())
					.to(targetTopic);
		} else { 
			stream.to(targetTopic);
		}

        LOGGER.info("Stream started here...");
        return stream;
    }
    
	/**
	 * Returns a transformer that computes running, ever-incrementing word counts.
	 */
	private static final class DedupTransformerSupplier
			implements TransformerSupplier<GenericRecord, GenericRecord, KeyValue<GenericRecord, GenericRecord>> {

		final private String stateStoreName;
		final private boolean isVerbose;

		public DedupTransformerSupplier(final String stateStoreName, boolean isVerbose) {
			this.stateStoreName = stateStoreName;
			this.isVerbose = isVerbose;
		}

		@Override
		public Transformer<GenericRecord, GenericRecord, KeyValue<GenericRecord, GenericRecord>> get() {
			return new Transformer<GenericRecord, GenericRecord, KeyValue<GenericRecord, GenericRecord>>() {

				private KeyValueStore<GenericRecord, Long> stateStore;
				private ProcessorContext ctx;

				@SuppressWarnings("unchecked")
				@Override
				public void init(final ProcessorContext context) {
					stateStore = (KeyValueStore<GenericRecord, Long>) context.getStateStore(stateStoreName);
					ctx = context;
				}

				@Override
				public KeyValue<GenericRecord, GenericRecord> transform(GenericRecord key, final GenericRecord value) {
					// For simplification (and unlike the traditional wordcount) we assume that the
					// value is
					// a single word, i.e. we don't split the value by whitespace into potentially
					// one or more
					// words.
					if (stateStore.get(key) == null || ctx.timestamp() > stateStore.get(key)) {
						if (isVerbose) {
							if (stateStore.get(key) == null) {
								System.out.println("inserting key " + key + " with timestamp " + ctx.timestamp() + " to state-store");
								System.out.println("==> new message forwared to sink topic .....");
							} else {
								System.out.println("updating key " + key + " with timestamp " + ctx.timestamp() + " in state-store (replacing previous timestamp " + stateStore.get(key) + ")");
								System.out.println("==> more actual message forwared to sink topic .....");
							}
						}
						stateStore.put(key, ctx.timestamp());
						return KeyValue.pair(key, value);
					} else {
						if (isVerbose) {
							System.out.println("retired message detected for key " + key + " with timestamp " + ctx.timestamp() + " (newer value with timestamp " + stateStore.get(key) + " seen before)");
							System.out.println("==> 'old' message removed.....");
						}
						return KeyValue.pair(key, null);
					}
				}

				@Override
				public void close() {
					// Note: The store should NOT be closed manually here via `stateStore.close()`!
					// The Kafka Streams API will automatically close stores when necessary.
				}
			};
		}

	}

	private static final class ValueIsNotNullPredicate implements Predicate<GenericRecord, GenericRecord> {

		@Override
		public boolean test(GenericRecord key, GenericRecord value) {
			return (value != null);
		}

	}    
}
