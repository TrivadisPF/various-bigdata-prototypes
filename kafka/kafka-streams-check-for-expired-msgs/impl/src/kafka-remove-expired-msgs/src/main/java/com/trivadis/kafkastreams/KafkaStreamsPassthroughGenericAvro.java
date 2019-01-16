package com.trivadis.kafkastreams;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

public class KafkaStreamsPassthroughGenericAvro {

	class IgnoreSerializationError implements ProductionExceptionHandler {

		@Override
		public void configure(Map<String, ?> config) {
		}

		@Override
		public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> record, Exception exception) {
			System.out.println(exception);
			return null;
		}
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
						if (isVerbose)
							System.out.println("adding key " + key + " with timestamp " + ctx.timestamp() + " with stateStore " + stateStore.get(key));
						stateStore.put(key, ctx.timestamp());
						return KeyValue.pair(key, value);
					} else {
						if (isVerbose)
							System.out.println("old value for key " + key + " with timestamp " + ctx.timestamp() + " with stateStore " + stateStore.get(key));
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
	
	public static final Topology getTopology(String sourceTopic, String targetTopic, String schemaRegistryUrl, 
										boolean isExpiredCheck, boolean isVerbose) {
		// In the subsequent lines we define the processing topology of the Streams
		// application.
		// used to be KStreamBuilder ....
		final StreamsBuilder builder = new StreamsBuilder();
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
			builder.addStateStore(dedupStore);
		}
		
		KStream<GenericRecord,GenericRecord> t = builder.stream(sourceTopic);
		
		// check if it should be checked for expiration of a record against the store
		if (isExpiredCheck) {
			t.transform(new DedupTransformerSupplier(dedupStore.name(), isVerbose)
													, dedupStore.name())
					.filter(new ValueIsNotNullPredicate())
					.to(targetTopic);
		} else { 
			t.to(targetTopic);
		}

		return builder.build();
	}

	private void run(String applicationId, String bootstrapServers, String sourceTopic, String targetTopic,
			String schemaRegistryUrl, boolean isExpiredCheck, boolean isVerbose) {
		final Properties streamsConfiguration = new Properties();
		// Give the Streams application a unique name. The name must be unique in the
		// Kafka cluster
		// against which the application is run.
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);

		// Where to find Kafka broker(s).
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		// Where to find the Confluent schema registry instance(s)
		streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

		// Specify default (de)serializers for record keys and for record values.
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
//		streamsConfiguration.put("default.deserialization.exception.handler", IgnoreSerializationError.class);
		
		// specify the TimestampExtrator to use
		// streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
		// JsonTimestampExtractor.class);

		Topology topology = getTopology(sourceTopic, targetTopic, schemaRegistryUrl, isExpiredCheck, isVerbose);
		
		// used to be new KafkaStreams(build, streamsConfiguration)
		final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);

		// clean up all local state by application-id
		streams.cleanUp();

		streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
			System.out.println("Within UncaughtExceptionHandler =======>");
			System.out.println(throwable);
			throwable.printStackTrace();
			// here you should examine the throwable/exception and perform an appropriate
			// action!
		});

		streams.start();

		// Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	public static void main(String[] args) {

		String applicationId = null;
		String bootstrapServer = null;
		String sourceTopic = null;
		String targetTopic = null;
		String schemaRegistryUrl = null;
		
		boolean isExpiredCheck = false;
		boolean isVerbose = false;

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		Options options = new Options();
		options.addOption("id", "application-id", true, "REQUIRED: The application id.");
		options.addOption("b", "bootstrap-server", true, "REQUIRED: The server(s) to connect to.");
		options.addOption("s", "source-topic", true, "REQUIRED: The topic to consume from.");
		options.addOption("t", "target-topic", true, "REQUIRED: The topic to write to.");
		options.addOption("sr", "schema-registry-url", true,
				"REQUIRED: the schema registry to connect to for the Avro schemas.");
		options.addOption("e", "expired-check", false, "OPTIONAL: Flag for  Defaults to false.");
		options.addOption("v", "verbose", false, "OPTIONAL: flag controlling if additional output information should be sent to stdout or stderr");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("application-id")) {
				applicationId = line.getOptionValue("application-id");
			}
			if (line.hasOption("bootstrap-server")) {
				bootstrapServer = line.getOptionValue("bootstrap-server");
			}
			if (line.hasOption("source-topic")) {
				sourceTopic = line.getOptionValue("source-topic");
			}
			if (line.hasOption("target-topic")) {
				targetTopic = line.getOptionValue("target-topic");
			}
			if (line.hasOption("schema-registry-url")) {
				schemaRegistryUrl = line.getOptionValue("schema-registry-url");
				System.out.println(schemaRegistryUrl);
			}
			if (line.hasOption("expired-check")) {
				isExpiredCheck = true;
			}
			if (line.hasOption("verbose")) {
				isVerbose = true;
			}

			KafkaStreamsPassthroughGenericAvro passthrough = new KafkaStreamsPassthroughGenericAvro();
			passthrough.run(applicationId, bootstrapServer, sourceTopic, targetTopic, schemaRegistryUrl, isExpiredCheck, isVerbose);
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("kafka-passthrough", exp.getMessage(), options, null, true);
		}

	}

}
