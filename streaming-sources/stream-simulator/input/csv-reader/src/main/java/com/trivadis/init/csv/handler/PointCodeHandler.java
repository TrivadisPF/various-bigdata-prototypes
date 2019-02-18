package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.PointCode;
import com.trivadis.init.csv.event.UserInteractionEvent;

public class PointCodeHandler extends AbstractCSVRecordHandler implements CSVRecordHandler {

	public boolean canHandle(String[] record) {
		return true;
	}

	public String getName() {
		return "PointCode";
	}
	
	public AbstractEvent handle(String[] record) {
		PointCode event = new PointCode(record[0]				// code
										, (record[1])	 		// shortName
										, (record[2])			// longName
										, toDouble(record[3])	// longitude
										, toDouble(record[4])	// latitude
										, record[5]				// pontType
										, record[6]				// districtCode
										, record[7]				// stopIdentifier
										, toInteger(record[8])	// positionNo
										, record[9]				// organisationalUnit
										, record[10]				// municipalty
										, record[11]				// streetDirection
										, record[12]				// street
										, record[13]				// streetCrossing
										, record[14]				// intersectionPosition
										, record[15]				// sheltered
										, record[16]				// barrierFree
									);
		return event;
	}

	
}
