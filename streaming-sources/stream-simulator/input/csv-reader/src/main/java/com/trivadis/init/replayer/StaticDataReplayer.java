package com.trivadis.init.replayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trivadis.init.csv.EventReader;
import com.trivadis.init.csv.EventReaderDRV;
import com.trivadis.init.csv.EventReaderEV;
import com.trivadis.init.csv.EventReaderPO;
import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.publish.EventPublisher;
import com.trivadis.init.publish.KafkaPublisher;

public class StaticDataReplayer {

	private List<EventPublisher> eventPublishers = new ArrayList<EventPublisher>();
	private List<EventReader> eventReaders = new ArrayList<EventReader>();

	private void publish(AbstractEvent event) {
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
	
	public StaticDataReplayer() {
		//this.eventPublisher = new DebugPublisher();
		addPublisher(new KafkaPublisher());
	}
	
	public void addReader (EventReader eventReader) {
		eventReaders.add(eventReader);
	}
	
	public void addPublisher(EventPublisher publisher) {
		eventPublishers.add(publisher);
	}
	
	public void start() throws IOException {
		
		for (EventReader evr: eventReaders) {
			while (evr.hasNext()) {
				publish(evr.getMessage());
			}
		}
	}
	
	
	public static void main(String args[]) throws IOException {
		StaticDataReplayer replayer = new StaticDataReplayer();

		replayer.addReader(new EventReaderPO("classpath:csv/PO20170923.csv"));
		
		replayer.start();
	}
}
