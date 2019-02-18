package com.trivadis.init.csv.handler;

import com.trivadis.init.csv.event.AbstractEvent;

public interface CSVRecordHandler {

	public boolean canHandle(String [] record);
	
	public String getName();
	public AbstractEvent handle(String[] record); 
}
