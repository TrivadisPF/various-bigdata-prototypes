package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.DataRadioVehicleEvent;

public class DataRadioVehicleHandler extends AbstractCSVRecordHandler implements CSVRecordHandler  {

	public boolean canHandle(String[] record) {
		return true;
	}
	
	public String getName() {
		return "DatatRadioVehicle";
	}

	public AbstractEvent handle(String[] record) {
		DataRadioVehicleEvent event = new DataRadioVehicleEvent(toDate(record[0])			// date
																, toInteger(record[1])		// actualTime
																, toInteger(record[2])		// radioId
																, (record[3])				// radioType
																, toInteger(record[4])		// messageType
																, (record[5])				// messageValue
																, (record[6])				// ipAddress
																, toInteger(record[7])		// deviceOwnerType
																, toDouble(record[8])		// actualGPSLongitude
																, toDouble(record[9])		// actualGPSLatitude
																, toDouble(record[10])		// actualGPSAltitude
																, (record[11])				// vehicle
																, (record[12])				// lineCode
																);

		return event;
	}


}
