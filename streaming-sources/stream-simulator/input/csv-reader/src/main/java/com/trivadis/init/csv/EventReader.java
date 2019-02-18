package com.trivadis.init.csv;

import java.io.IOException;

import com.trivadis.init.csv.event.AbstractEvent;

public interface EventReader {

	public boolean hasNext() throws IOException;

	public Integer getEventTime();

	public AbstractEvent getMessage();

}