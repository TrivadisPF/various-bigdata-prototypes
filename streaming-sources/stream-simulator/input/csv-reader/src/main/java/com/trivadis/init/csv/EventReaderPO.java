package com.trivadis.init.csv;

import java.io.FileNotFoundException;

import com.trivadis.init.csv.handler.E_UserInteractionHandler;
import com.trivadis.init.csv.handler.H_HaltHandler;
import com.trivadis.init.csv.handler.PointCodeHandler;
import com.trivadis.init.csv.handler.T_PassengerPerDoorHandler;
import com.trivadis.init.csv.handler.Z_CyclicMessageHandler;

public class EventReaderPO extends AbstractEventReader implements EventReader {

	public EventReaderPO(String eventFile) throws FileNotFoundException {
		// skip the first 8 lines in the file
		super(eventFile, 0);
		registerHandler(new PointCodeHandler());
	}
}
