package com.trivadis.init.csv.event;

import java.sql.Time;
import java.time.LocalTime;

public abstract class AbstractEvent {
	public abstract Integer getEventTimestamp();

	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public Time getRealTimestamp() {
		return Time.valueOf(LocalTime.MIDNIGHT.plusSeconds(getEventTimestamp()));
	}
}
