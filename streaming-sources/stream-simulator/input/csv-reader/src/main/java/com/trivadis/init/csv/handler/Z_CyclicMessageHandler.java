package com.trivadis.init.csv.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.CyclicMessageEvent;

public class Z_CyclicMessageHandler extends AbstractCSVRecordHandler implements CSVRecordHandler {

	public boolean canHandle(String[] record) {
		return record[0].equals("Z");
	}
	
	public String getName() {
		return "CyclicMessage";
	}
	
	public AbstractEvent handle(String[] record) {
		CyclicMessageEvent event = new CyclicMessageEvent(record[1]						// vehicleCode
												, toInteger(record[2])	 	// odometer
												, toInteger(record[3])		// actualTime
												, toInteger(record[4])		// velocity
												, toInteger(record[5])		// direction
												, toDouble(record[6])		// actualGPSLongitude
												, toDouble(record[7])		// actualGPSLatitude
												, toDouble(record[8]	)		// actualGPSAltitude
												, toInteger(record[9])		// actualGPSSatelites
												, toInteger(record[10])		// actualGPSQuality
												, toDouble(record[11])		// actualGPSHDOP
												, toDouble(record[12])		// mapGPSLatitude
												, toDouble(record[13])		// mapGPSLogitude
												, toDouble(record[14])		// mapGPSAltitude
												, (record[15])				// radioType
												, toInteger(record[16])		// singalStrength
												, toInteger(record[17])		// bitErrorRate
												, toInteger(record[18])		// coverage
												, toInteger(record[19])		// provider
												, toInteger(record[20])		// signalToNoiseRatio
												, toInteger(record[21])		// scheduleDeviation
												, (record[22])				// baseStation
									);

		return event;
	}
	
	
}
