package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;
import com.trivadis.init.csv.event.UserInteractionEvent;

public class E_UserInteractionHandler extends AbstractCSVRecordHandler implements CSVRecordHandler {

	public boolean canHandle(String[] record) {
		return record[0].equals("E");
	}

	public String getName() {
		return "UserInteraction";
	}
	
	public AbstractEvent handle(String[] record) {
		UserInteractionEvent event = new UserInteractionEvent(record[1]					// vehicleCode
														, toInteger(record[2])	 	// odometer
														, toInteger(record[3])		// actualTime
														, toInteger(record[4])		// nominalTime
														, toInteger(record[5])		// interactionType
														, record[6]					// value1
														, record[7]					// value2
														, record[8]					// value3
														, record[9]					// value4
														, record[10]					// value5
									);
		return event;
	}

	public enum InteractionTypeEnum {
		LOG_OFF(0),
		LOG_ON(1),
		PAUSE_START(2),
		PAUSE_END(3),
		COLDED_MESSAGE(4),
		SPEECH_REQUEST(5),
		SPECIAL_BUTTON(6);
		
		private int interactionType;
		
		private InteractionTypeEnum(int interactionType) {
			this.interactionType = interactionType;
		}
	}


	
}
