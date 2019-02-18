package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.HaltEvent;
import com.trivadis.init.csv.event.PassengerPerDoorEvent;
import com.trivadis.init.csv.event.UserInteractionEvent;

public class T_PassengerPerDoorHandler extends AbstractCSVRecordHandler implements CSVRecordHandler {

	public boolean canHandle(String[] record) {
		return record[0].equals("T");
	}

	public String getName() {
		return "PassengerPerDoor";
	}
	
	public AbstractEvent handle(String[] record) {
		int i = 1;
		PassengerPerDoorEvent event = new PassengerPerDoorEvent(record[i++]		// vehicleCode
									, toInteger(record[i++])	 					// odometer
									, toInteger(record[i++])						// actualTime
									, toInteger(record[i++])						// nominalArrivalTime
									, toInteger(record[i++])						// nominalDepartureTime
									, (record[i++])								// stopCode
									, toInteger(record[i++])						// positionNo
									, toInteger(record[i++])						// wagonNo
									, toInteger(record[i++])						// doorNo
									, toInteger(record[i++])						// boardings
									, toInteger(record[i++])						// alightings
									);
		return event;
	}



	
}
