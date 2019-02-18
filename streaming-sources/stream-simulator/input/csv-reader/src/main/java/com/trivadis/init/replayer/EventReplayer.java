package com.trivadis.init.replayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.trivadis.init.csv.EventReader;
import com.trivadis.init.csv.EventReaderDRV;
import com.trivadis.init.csv.EventReaderEV;
import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.publish.DebugPublisher;
import com.trivadis.init.publish.EventPublisher;
import com.trivadis.init.publish.KafkaPublisher;
import com.trivadis.init.publish.MQTTPublisher;

public class EventReplayer {
	
//	private static PropertyParser propertyParser;
	private static final boolean DO_CLEAN_UP = true;

	public static final String PROGRAM_NAME = "Lab";

	public static final String SINK_NAME_SHORT_FLAG = "-s";
	public static final String SINK_NAME_LONG_FLAG = "--sink";

	public static final String HELP_SHORT_FLAG_1 = "-?";
	public static final String HELP_SHORT_FLAG_2 = "-h";
	public static final String HELP_LONG_FLAG = "--help";

	public static final boolean ALL = false;
	public static final boolean COMPACT = true;

	public static final String KAFKA = "kafka";
	public static final String MQTT = "mqtt";
	
	private Integer currentEventTimestamp = null;
	private Integer startWithEventTimestamp = 0;
	private Integer eventTimeSpeedUpFactor = 1;

	private List<EventPublisher> eventPublishers = new ArrayList<EventPublisher>();
	private List<EventReader> eventReaders = new ArrayList<EventReader>();
	
	private void waitUntilEventTimeReached(AbstractEvent event) {
		Integer waitInSec = (event.getEventTimestamp() - currentEventTimestamp) / eventTimeSpeedUpFactor;			
		System.out.println("Next event " + event.getClass() + " at " + event.getEventTimestamp() + " in " + waitInSec);
		try {
			Thread.sleep(waitInSec * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			currentEventTimestamp = event.getEventTimestamp();
		}
	}

	private void publish(AbstractEvent event) {
		// wait until the event time of the message is reached
		waitUntilEventTimeReached(event);
		
		// map to JSON
		ObjectMapper mapper = new ObjectMapper();
		String channelName = event.getName();

		try {
			String payload = mapper.writeValueAsString(event);
			for (EventPublisher eventPublisher : eventPublishers) {
				eventPublisher.send(channelName, payload);		
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public EventReplayer(Integer startWithEventTimestamp, Integer eventTimeSpeedUpFactor) {
		this.startWithEventTimestamp = startWithEventTimestamp;
		this.eventTimeSpeedUpFactor = eventTimeSpeedUpFactor;
		
	}
	
	public void addReader (EventReader eventReader) {
		eventReaders.add(eventReader);
	}
	
	public void addPublisher(EventPublisher publisher) {
		eventPublishers.add(publisher);
	}
	
	public void start() throws IOException {
		currentEventTimestamp = startWithEventTimestamp;
		
//		Map<EventReader,Integer> timestampByEventReader = new HashMap<EventReader,Integer>();
		for (EventReader evr: eventReaders) {
			while (evr.hasNext()) {
				if (startWithEventTimestamp < evr.getEventTime()) {
					break;
				}
			}
		}
		
		while (true) {
			Integer smallestTimestamp = Integer.MAX_VALUE;
			EventReader smallestReader = null;
			
			for (EventReader evr:  eventReaders) {
				if (evr.getEventTime() != Integer.MIN_VALUE) {
					if (evr.getEventTime() < smallestTimestamp) {
						smallestTimestamp = evr.getEventTime();
						smallestReader = evr;
					}
				}
			}
			publish(smallestReader.getMessage());
			smallestReader.hasNext();
		}
	}
	
	
	public static void main(String args[]) throws IOException {
		String sink = KAFKA;

		boolean compact = ALL;

//		long iterations = 1;
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
			case HELP_SHORT_FLAG_1:
			case HELP_SHORT_FLAG_2:
			case HELP_LONG_FLAG:
				usage();
				break;
			default:
				System.err.printf("%s: %s: unrecognized option%n%n", PROGRAM_NAME, flag);
				usage(1);
			}
		}		
		
		// start at 28800 with a replay factor of 1
		EventReplayer reader = new EventReplayer(28800, 1);

		// addPublisher(new DebugPublisher());
		if (sink.equals(KAFKA)) {
			reader.addPublisher(new KafkaPublisher());
		} else if (sink.equals(MQTT)) {
			reader.addPublisher(new MQTTPublisher());
		}

		reader.addReader(new EventReaderEV("classpath:csv/ev20170923sorted.csv"));
		reader.addReader(new EventReaderDRV("classpath:csv/DRV20170923sorted.csv"));
		
		reader.start();
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
