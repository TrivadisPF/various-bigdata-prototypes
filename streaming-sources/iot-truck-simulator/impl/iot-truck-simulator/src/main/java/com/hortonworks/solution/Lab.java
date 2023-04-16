package com.hortonworks.solution;

import java.io.IOException;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.hortonworks.labutils.RangeExpander;
import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hortonworks.labutils.PropertyParser;
import com.hortonworks.labutils.SensorEventsGenerator;
import com.hortonworks.labutils.SensorEventsParam;

public class Lab {

	private static final Logger LOG = LoggerFactory.getLogger(Lab.class);
	private static PropertyParser propertyParser;
	private static final boolean DO_CLEAN_UP = true;

	public static final String PROGRAM_NAME = "iot-truck-simulator";

	public static final String BROKER_HOST_SHORT_FLAG = "-h";
	public static final String BROKER_HOST_LONG_FLAG = "--host";

	public static final String SINK_NAME_SHORT_FLAG = "-s";
	public static final String SINK_NAME_LONG_FLAG = "--sink";

	public static final String SECURITY_PROTOCOL_LONG_FLAG = "--securityProtocol";
	public static final String SASL_MECHANISM_LONG_FLAG = "--saslMechanism";
	public static final String SASL_USERNAME_LONG_FLAG = "--saslUsername";
	public static final String SASL_PASSWORD_LONG_FLAG = "--saslPassword";

	public static final String FORMAT_NAME_SHORT_FLAG = "-f";
	public static final String FORMAT_NAME_LONG_FLAG = "--format";

	public static final String SCHEMA_REGISTRY_KIND = "--schemaRegistryKind";
	public static final String SCHEMA_REGISTRY_URL = "--schemaRegistryUrl";
	public static final String AZURE_TENANT_ID = "--azTenantId";
	public static final String AZURE_CLIENT_ID = "--azClientId";
	public static final String AZURE_CLIENT_SECRET = "--azClientSecret";
	public static final String AZURE_SCHEMA_GROUP = "--azSchemaGroup";

	public static final String PORT_NAME_SHORT_FLAG = "-p";
	public static final String PORT_NAME_LONG_FLAG = "--port";

	public static final String MODE_NAME_SHORT_FLAG = "-m";
	public static final String MODE_NAME_LONG_FLAG = "--mode";

	public static final String TIME_RESOLUTION_NAME_SHORT_FLAG = "-t";
	public static final String TIME_RESOLUTION_NAME_LONG_FLAG = "--timeres";

	public static final String MESSAGE_TYPE_SHORT_FLAG = "-mt";
	public static final String MESSAGE_TYPE_LONG_FLAG = "--messageType";

	public static final String FLEET_SIZE_SHORT_FLAG = "-fs";
	public static final String FLEET_SIZE_LONG_FLAG = "--fleetSize";

	public static final String VEHICLE_FILTER_SHORT_FLAG = "-vf";
	public static final String VEHICLE_FILTER_LONG_FLAG = "--vehicleFilter";

	public static final String DELAY_SHORT_FLAG = "-d";
	public static final String DELAY_LONG_FLAG = "--delay";

	public static final String FILE_PER_VEHICLE_SHORT_FLAG = "-fpv";
	public static final String FILE_PER_VEHICLE_LONG_FLAG = "--filePerVehicle";

	public static final String DEVICE_ID_SHORT_FLAG = "-did";
	public static final String DEVICE_ID_LONG_FLAG = "--deviceId";

	public static final String AZ_ACCESS_KEY_SHORT_FLAG = "-ak";
	public static final String AZ_ACCESS_KEY_LONG_FLAG = "--accessKey";

	public static final String EVENT_SCHEMA_SHORT_FLAG = "-es";
	public static final String EVENT_SCHEMA_LONG_FLAG = "--eventSchema";

	public static final String TOPIC_NAMES_SHORT_FLAG = "-topics";
	public static final String TOPIC_NAMES_LONG_FLAG = "--topicNames";

	public static final String HELP_SHORT_FLAG_1 = "-?";
	public static final String HELP_LONG_FLAG = "--help";

	public static final boolean ALL = false;
	public static final boolean COMPACT = true;

	public static final String STDOUT = "stdout";
	public static final String FILE = "file";
	public static final String KAFKA = "kafka";
	public static final String MQTT = "mqtt";
	public static final String JMS = "jms";
	public static final String RABBITMQ = "rabbitmq";
	public static final String AZURE_IOT_HUB = "az-iothub";

	public static final String CSV = "csv";
	public static final String JSON = "json";
	public static final String AVRO = "avro";

	public static final String CONFLUENT = "confluent";
	public static final String AZURE = "azure";

	public static final String COMBINE = "combine";
	public static final String SPLIT = "split";
	
	public static final String TIME_RESOLUTION_MS = "millisec";
	public static final String TIME_RESOLUTION_S = "sec";

	public static final String TEXT = "text";
	public static final String MAP = "map";
	public static final String BYTES = "bytes";

	public static String host = "";
	public static String securityProtocol = "";
	public static String saslMechanism = "";
	public static String saslUsername = "";
	public static String saslPassword = "";
	public static String schemaRegistryKind = CONFLUENT;
	public static String schemaRegistryUrl = "";

	public static String azureSchemaGroup = "";
	public static String azureTenantId = "";
	public static String azureClientId = "";
	public static String azureClientSecret = "";

	public static String format = CSV;
	public static String port = "";
	public static String mode = COMBINE;
	public static String timeResolution = TIME_RESOLUTION_S;
	public static String messageType = TEXT;
	public static int eventSchema = MobileEyeEvent.EVENT_SCHEMA_1;

	public static String topicName = "truck_position,truck_driving_info";

	public static int truckFleetSize = 100;
	public static int delayBetweenEventsMs = 4000;

	public static Integer delay;
	public static Integer fleetSize;
	public static List<Integer> vehicleFilters = null;
	public static boolean filePerTruck = false;
	public static String deviceId;
	public static String accessKey;

	static {
		try {
			propertyParser = new PropertyParser("default.properties");
			propertyParser.parsePropsFile();
		} catch (IOException e) {
			// LOG.error("Unable to load property file: " +
			// Launcher.class.getResource("/default.properties").getPath());
		}
	}

	private static List<Integer> expand(String s)
	{
		List<Integer> ints = new ArrayList<>();
		String p = s;
		String[] arr = p.split("\\-");
		String k = "";
		for (int i = 0; i < arr.length; i++) {
			if (i != arr.length - 1) {
				String[] arr1 = arr[i].split(", ");
				String[] arr2 = arr[i + 1].split(", ");
				int a = Integer.parseInt(arr1[arr1.length - 1]);
				int b = Integer.parseInt(arr2[0]);
				for (int j = a + 1; j < b; j++) {
					arr[i] = arr[i] + ", " + j;
				}
			}
			if (k != "")
				k = k + ", " + arr[i];
			else
				k = k + arr[i];
		}
		String[] karr = k.split(",");
		for (int i = 0; i<karr.length; i++) {
			ints.add(Integer.valueOf(karr[i].trim()));
		}
		return ints;
	}

	protected static List<Integer> toListOfInts(String value) {
		String valueNoWS = StringUtils.deleteWhitespace(value);
		// expand ranges
		RangeExpander re = new RangeExpander(valueNoWS);

		// add to list
		List<Integer> ints = ImmutableList.copyOf((Iterator<? extends Integer>) re);
		return ints;
	}

	public static void main(String args[]) {
		String sink = KAFKA;
//		String format = CSV;
		
		boolean compact = ALL;

		long iterations = 1;
		String outputFile = null;

		Iterator<String> argv = Arrays.asList(args).iterator();
		while (argv.hasNext()) {
			String flag = argv.next();
			switch (flag) {
			case SINK_NAME_SHORT_FLAG:
			case SINK_NAME_LONG_FLAG:
				sink = null;
				sink = nextArg(argv, flag).toLowerCase();
				break;
			case BROKER_HOST_SHORT_FLAG:
			case BROKER_HOST_LONG_FLAG:
				host = null;
				host = nextArg(argv, flag).toLowerCase();
				break;
			case SECURITY_PROTOCOL_LONG_FLAG:
				securityProtocol = nextArg(argv, flag);
				break;
			case SASL_MECHANISM_LONG_FLAG:
				saslMechanism = nextArg(argv, flag);
				break;
			case SASL_USERNAME_LONG_FLAG:
				saslUsername = nextArg(argv, flag);
				break;
			case SASL_PASSWORD_LONG_FLAG:
				saslPassword = nextArg(argv, flag);
				break;
			case SCHEMA_REGISTRY_KIND:
				schemaRegistryKind = nextArg(argv, flag).toLowerCase();
				break;
			case SCHEMA_REGISTRY_URL:
				schemaRegistryUrl = nextArg(argv, flag);
				break;
			case AZURE_TENANT_ID:
				azureTenantId = nextArg(argv, flag);
				break;
			case AZURE_CLIENT_ID:
				azureClientId = nextArg(argv, flag);
				break;
			case AZURE_CLIENT_SECRET:
				azureClientSecret = nextArg(argv, flag);
				break;
			case AZURE_SCHEMA_GROUP:
				azureSchemaGroup = nextArg(argv, flag);
				break;
			case FORMAT_NAME_SHORT_FLAG:
			case FORMAT_NAME_LONG_FLAG:
				format = null;
				format = nextArg(argv, flag).toLowerCase();
				break;				
			case PORT_NAME_SHORT_FLAG:
			case PORT_NAME_LONG_FLAG:
				port = null;
				port = nextArg(argv, flag).toLowerCase();
				break;				
			case MODE_NAME_SHORT_FLAG:
			case MODE_NAME_LONG_FLAG:
				mode = null;
				mode = nextArg(argv, flag).toLowerCase();
				break;				
			case TIME_RESOLUTION_NAME_SHORT_FLAG:
			case TIME_RESOLUTION_NAME_LONG_FLAG:
				timeResolution = null;
				timeResolution = nextArg(argv, flag).toLowerCase();
				break;				
			case MESSAGE_TYPE_SHORT_FLAG:
			case MESSAGE_TYPE_LONG_FLAG:
				messageType = null;
				messageType = nextArg(argv, flag).toLowerCase();
				break;
			case FLEET_SIZE_SHORT_FLAG:
			case FLEET_SIZE_LONG_FLAG:
				fleetSize = null;
				fleetSize = Integer.valueOf(nextArg(argv, flag).toLowerCase());
				break;
			case VEHICLE_FILTER_SHORT_FLAG:
			case VEHICLE_FILTER_LONG_FLAG:
				vehicleFilters = new ArrayList<Integer>();
				vehicleFilters = toListOfInts(nextArg(argv, flag).toLowerCase());
				break;
			case DELAY_SHORT_FLAG:
			case DELAY_LONG_FLAG:
				String delayBetweenEventsMsString = nextArg(argv, flag).trim();
				delayBetweenEventsMs = Integer.valueOf(delayBetweenEventsMsString);
				break;
			case DEVICE_ID_SHORT_FLAG:
			case DEVICE_ID_LONG_FLAG:
				deviceId = nextArg(argv, flag).toLowerCase();
				break;
			case AZ_ACCESS_KEY_SHORT_FLAG:
			case AZ_ACCESS_KEY_LONG_FLAG:
				accessKey = nextArg(argv, flag);
				break;
			case FILE_PER_VEHICLE_SHORT_FLAG:
			case FILE_PER_VEHICLE_LONG_FLAG:
				filePerTruck = true;
				break;
			case EVENT_SCHEMA_SHORT_FLAG:
			case EVENT_SCHEMA_LONG_FLAG:
				eventSchema = Integer.valueOf(nextArg(argv, flag).trim());
				break;
			case TOPIC_NAMES_SHORT_FLAG:
			case TOPIC_NAMES_LONG_FLAG:
				topicName = nextArg(argv, flag) + ",none";		// two topics can be specified, make sure that there is a spearator if only one topic is specified
				break;
			case HELP_SHORT_FLAG_1:
			case HELP_LONG_FLAG:
				usage();
				break;
			default:
				System.err.printf("%s: %s: unrecognized option%n%n", PROGRAM_NAME, flag);
				usage(1);
			}
		}

		SensorEventsParam sensorEventsParam = new SensorEventsParam();
		sensorEventsParam.setEventEmitterClassName("com.hortonworks.simulator.impl.domain.transport.Truck");

		if (sink.equals(KAFKA)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.KafkaSensorEventCollector");
		} else if (sink.equals(MQTT)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.MQTTSensorEventCollector");
		} else if (sink.equals(JMS)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.ActiveMQSensorEventCollector");
		} else if (sink.equals(RABBITMQ)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.RabbitMQSensorEventCollector");
		} else if (sink.equals(AZURE_IOT_HUB)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.AzureIoTHubSensorEventCollector");
		} else if (sink.equals(STDOUT)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.StdOutSensorEventCollector");
		} else if (sink.equals(FILE)) {
			sensorEventsParam.setEventCollectorClassName("com.hortonworks.solution.FileSensorEventCollector");
		} else {
			throw new IllegalArgumentException("sink needs to be one of KAFKA, MQTT, JMS, RABBITMQ, AZ-IOTHUB, FILE or STDOUT, but was " + sink);
		}
		sensorEventsParam.setNumberOfEvents(1000);
		sensorEventsParam.setDelayBetweenEvents(delayBetweenEventsMs);
		System.out.println(Lab.class.getResource("/" + "routes/midwest").getPath());
		sensorEventsParam.setRouteDirectory(Lab.class.getResource("/" + "routes/midwest").getPath());
//		sensorEventsParam.setRouteDirectory("/Users/gus/workspace/git/gschmutz/various-demos/iot-truck-simulator/src/main/resources/routes/midwest");
		sensorEventsParam.setTruckSymbolSize(10000);
		SensorEventsGenerator sensorEventsGenerator = new SensorEventsGenerator();
		sensorEventsGenerator.generateTruckEventsStream(sensorEventsParam);

		while (true) {
			// run until ctrl-c'd or stopped from IDE
		}
	}

	private static String nextArg(Iterator<String> argv, String flag) {
		if (!argv.hasNext()) {
			System.err.printf("%s: %s: argument required%n", PROGRAM_NAME, flag);
			usage(1);
		}
		return argv.next();
	}

	private static void usage() {
		usage(0);
	}

	private static void usage(int exitValue) {
		String header = String.format("%s: Generate random Avro data%n", PROGRAM_NAME);
/*
		String summary = String.format(
				"Usage: %s [%s <file> | %s <schema>] [%s | %s] [%s | %s] [%s <i>] [%s <file>]%n%n", PROGRAM_NAME,
				SINK_NAME_SHORT_FLAG, SCHEMA_SHORT_FLAG, JSON_SHORT_FLAG, BINARY_SHORT_FLAG, PRETTY_SHORT_FLAG,
				COMPACT_SHORT_FLAG, ITERATIONS_SHORT_FLAG, OUTPUT_FILE_SHORT_FLAG);

		final String indentation = "    ";
		final String separation = "\t";
		String flags = "Flags:\n"
				+ String.format("%s%s, %s, %s:%s%s%n", indentation, HELP_SHORT_FLAG_1, HELP_SHORT_FLAG_2,
						HELP_LONG_FLAG, separation, "Print a brief usage summary and exit with status 0")
				+ String.format("%s%s, %s:%s%s%n", indentation, BINARY_SHORT_FLAG, BINARY_LONG_FLAG, separation,
						"Encode outputted data in binary format")
				+ String.format("%s%s, %s:%s%s%n", indentation, COMPACT_SHORT_FLAG, COMPACT_LONG_FLAG, separation,
						"Output each record on a single line of its own (has no effect if encoding is not JSON)")
				+ String.format("%s%s <file>, %s <file>:%s%s%n", indentation, SCHEMA_FILE_SHORT_FLAG,
						SCHEMA_FILE_LONG_FLAG, separation,
						"Read the schema to spoof from <file>, or stdin if <file> is '-' (default is '-')")
				+ String.format("%s%s <i>, %s <i>:%s%s%n", indentation, ITERATIONS_SHORT_FLAG, ITERATIONS_LONG_FLAG,
						separation, "Output <i> iterations of spoofed data (default is 1)")
				+ String.format("%s%s, %s:%s%s%n", indentation, JSON_SHORT_FLAG, JSON_LONG_FLAG, separation,
						"Encode outputted data in JSON format (default)")
				+ String.format("%s%s <file>, %s <file>:%s%s%n", indentation, OUTPUT_FILE_SHORT_FLAG,
						OUTPUT_FILE_LONG_FLAG, separation,
						"Write data to the file <file>, or stdout if <file> is '-' (default is '-')")
				+ String.format("%s%s, %s:%s%s%n", indentation, PRETTY_SHORT_FLAG, PRETTY_LONG_FLAG, separation,
						"Output each record in prettified format (has no effect if encoding is not JSON)" + "(default)")
				+ String.format("%s%s <schema>, %s <schema>:%s%s%n", indentation, SCHEMA_SHORT_FLAG, SCHEMA_LONG_FLAG,
						separation, "Spoof the schema <schema>")
				+ "\n";

		String footer = String.format("%s%n%s%n", "Currently on Chris Egerton's public GitHub:",
				"https://github.com/C0urante/avro-random-generator");

		System.err.printf(header + summary + flags + footer);
		System.exit(exitValue);
*/
	}
}
