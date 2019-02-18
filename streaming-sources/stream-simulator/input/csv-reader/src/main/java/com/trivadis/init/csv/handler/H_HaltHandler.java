package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.HaltEvent;
import com.trivadis.init.csv.event.UserInteractionEvent;

public class H_HaltHandler extends AbstractCSVRecordHandler implements CSVRecordHandler {

	public boolean canHandle(String[] record) {
		return record[0].equals("H");
	}

	public String getName() {
		return "Halt";
	}
	
	public AbstractEvent handle(String[] record) {
		int i = 1;
		HaltEvent halt = new HaltEvent(record[1]				// vehicleCode
									, toInteger(record[2])	 	// odometer
									, toInteger(record[3])		// actualArrivalTime
									, toInteger(record[4])		// nominalArrivalTime
									, toInteger(record[5])		// actualDepartureTime
									, toInteger(record[6])		// nominalDepartureTime
									, (record[7])				// stopCode
									, toInteger(record[8])		// positionNo
									, toInteger(record[9])		// scheduledDistance
									, toInteger(record[10])		// doorCriteria
									, toInteger(record[11])		// locationType
									, toInteger(record[12])		// stopType
									, toInteger(record[13])		// lastStopFlag
									, toDouble(record[14])		// actualGPSLongitude
									, toDouble(record[15])		// actualGPSLatitude
									, toInteger(record[16])		// patternIndex
									, (record[17])				// patternPostition
									, toInteger(record[18])		// doorOpenTime
									, (record[19])				// pointCode
									, (record[20])				// pointRole
									, (record[21])				// action
									, (record[22])				// planningType
//									, (record[23])				// note
//									, toInteger(record[24])		// correctionType
									);
		return halt;
	}



	
}
