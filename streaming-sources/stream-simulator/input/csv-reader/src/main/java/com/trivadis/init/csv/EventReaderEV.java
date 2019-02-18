package com.trivadis.init.csv;

import java.io.FileNotFoundException;

import com.trivadis.init.csv.handler.E_UserInteractionHandler;
import com.trivadis.init.csv.handler.H_HaltHandler;
import com.trivadis.init.csv.handler.T_PassengerPerDoorHandler;
import com.trivadis.init.csv.handler.Z_CyclicMessageHandler;

public class EventReaderEV extends AbstractEventReader implements EventReader {

	public EventReaderEV(String eventFile) throws FileNotFoundException {
		// skip the first 8 lines in the file
		super(eventFile, 8);
		registerHandler(new E_UserInteractionHandler());
		registerHandler(new Z_CyclicMessageHandler());
		registerHandler(new H_HaltHandler());
		registerHandler(new T_PassengerPerDoorHandler());
	}
}
