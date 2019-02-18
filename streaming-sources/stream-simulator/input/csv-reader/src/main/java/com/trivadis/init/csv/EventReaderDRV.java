package com.trivadis.init.csv;

import java.io.FileNotFoundException;

import com.trivadis.init.csv.handler.DataRadioVehicleHandler;

public class EventReaderDRV extends AbstractEventReader implements EventReader {

	public EventReaderDRV(String eventFile) throws FileNotFoundException {
		super(eventFile, 8);
		registerHandler(new DataRadioVehicleHandler());
	}
}
