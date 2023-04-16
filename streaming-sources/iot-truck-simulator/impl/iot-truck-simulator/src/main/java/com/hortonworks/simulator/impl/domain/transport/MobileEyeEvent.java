package com.hortonworks.simulator.impl.domain.transport;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hortonworks.simulator.impl.domain.Event;
import com.hortonworks.simulator.impl.domain.gps.Location;
import com.hortonworks.solution.Lab;
import com.trivadis.iot.trucking.avro.v1.TruckDrivingInfo;
import com.trivadis.iot.trucking.avro.v1.TruckPosition;
import com.trivadis.iot.trucking.avro.v1.TruckPositionAndDrivingInfo;

public class MobileEyeEvent extends Event {
	public final static Integer EVENT_KIND_BEHAVIOUR_AND_POSITION=1;
	public final static Integer EVENT_KIND_POSITION=2;
	public final static Integer EVENT_KIND_BEHAVIOUR=3;

	public final static Integer EVENT_SCHEMA_1=1;
	public final static Integer EVENT_SCHEMA_2=2;

	private MobileEyeEventTypeEnum eventType;
	private Truck truck;
	private Location location;
	private long correlationId;

	private String toCSV1(Integer eventKind, String timeResoultion) {
		Integer timeFactor = (timeResoultion.equals(Lab.TIME_RESOLUTION_S)) ? 1 : 1000;
		String result = null;

		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION)) {
			result = truck.toCSV(timeFactor) + eventType.toCSV() + ","
					+ location.getLatitude() + "," + location.getLongitude() + "," + correlationId;
		} else if (eventKind.equals(EVENT_KIND_POSITION)){
			result = new Date().getTime() * timeFactor + "," + truck.getTruckId() + "," + location.getLatitude() + "," + location.getLongitude();
		} else if (eventKind.equals(EVENT_KIND_BEHAVIOUR)){
			result = new Date().getTime() * timeFactor + "," + truck.getTruckId() + eventType.toCSV() + "," + correlationId;
		}
		return result;
	}

	private String toCSV2(Integer eventKind, String timeResoultion) {
		Integer timeFactor = (timeResoultion.equals(Lab.TIME_RESOLUTION_S)) ? 1 : 1000;
		String result = null;

		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION)) {
			result = "SystemB" + "," + truck.toCSV(timeFactor) + eventType.toCSV() + ","
					+ location.getLatitude() + ":" + location.getLongitude() + "," + correlationId;
		} else if (eventKind.equals(EVENT_KIND_POSITION)){
			result = "SystemB" + "," + new Date().getTime() * timeFactor + "," + truck.getTruckId() + "," + location.getLatitude() + ":" + location.getLongitude();
		} else if (eventKind.equals(EVENT_KIND_BEHAVIOUR)){
			result = "SystemB" + "," + new Date().getTime() * timeFactor + "," + truck.getTruckId() + eventType.toCSV() + "," + correlationId;
		}
		return result;
	}

	private String toJSON1(Integer eventKind, String timeResoultion) {
		Integer timeFactor = (timeResoultion.equals(Lab.TIME_RESOLUTION_S)) ? 1 : 1000;

		StringBuffer msg = new StringBuffer();
		msg.append("{");
		msg.append("\"timestamp\":" + new Date().getTime() * timeFactor);
		msg.append(",");
		msg.append("\"truckId\":" + truck.getTruckId());
		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION) || eventKind.equals(EVENT_KIND_BEHAVIOUR)) {
			msg.append(",");
			msg.append("\"driverId\":" + truck.getDriver().getDriverId());
			msg.append(",");
			msg.append("\"routeId\":" + truck.getDriver().getRoute().getRouteId());
			msg.append(",");
			msg.append("\"eventType\":\"" + eventType + "\"");
			msg.append(",");
			msg.append("\"correlationId\":\"" + correlationId + "\"");
		}
		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION) || eventKind.equals(EVENT_KIND_POSITION)) {
			msg.append(",");
			msg.append("\"latitude\":" + location.getLatitude());
			msg.append(",");
			msg.append("\"longitude\":" + location.getLongitude());
		}

		msg.append("}");
		return msg.toString();
	}

	private String toJSON2(Integer eventKind, String timeResoultion) {
		Integer timeFactor = (timeResoultion.equals(Lab.TIME_RESOLUTION_S)) ? 1 : 1000;

		StringBuffer msg = new StringBuffer();
		msg.append("{");
		msg.append("\"system\":\"" + "SystemB" + "\"");
		msg.append(",");
		msg.append("\"timestamp\":" + new Date().getTime() * timeFactor);
		msg.append(",");
		msg.append("\"truckId\":" + truck.getTruckId());
		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION) || eventKind.equals(EVENT_KIND_BEHAVIOUR)) {
			msg.append(",");
			msg.append("\"driverId\":" + truck.getDriver().getDriverId());
			msg.append(",");
			msg.append("\"routeId\":" + truck.getDriver().getRoute().getRouteId());
			msg.append(",");
			msg.append("\"eventType\":\"" + eventType + "\"");
			msg.append(",");
			msg.append("\"correlationId\":\"" + correlationId + "\"");
		}
		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION) || eventKind.equals(EVENT_KIND_POSITION)) {
			msg.append(",");
			msg.append("\"latLong\":\"" + location.getLatitude() +  ":" + location.getLongitude() + "\"");
		}

		msg.append("}");
		return msg.toString();
	}

	private Object toAVRO1(Integer eventKind, String timeResoultion) {
		Integer timeFactor = (timeResoultion.equals(Lab.TIME_RESOLUTION_S)) ? 1 : 1000;

		if (eventKind.equals(EVENT_KIND_BEHAVIOUR_AND_POSITION)) {
			TruckPositionAndDrivingInfo truckPosition = TruckPositionAndDrivingInfo.newBuilder()
					.setTimestamp(new Date().getTime() * timeFactor)
					.setTruckId(truck.getTruckId())
					.setDriverId(truck.getDriver().getDriverId())
					.setEventType(this.eventType.toString())
					.setRouteId(truck.getDriver().getRoute().getRouteId())
					.setLatitude(location.getLatitude())
					.setLongitude(location.getLatitude())
					.setCorrelationId(correlationId).build();

			return truckPosition;
		} else if (eventKind.equals(EVENT_KIND_POSITION)) {
			TruckPosition truckPosition = TruckPosition.newBuilder()
					.setTimestamp(new Date().getTime() * timeFactor)
					.setTruckId(truck.getTruckId())
					.setLatitude(location.getLatitude())
					.setLongitude(location.getLatitude()).build();
			return truckPosition;
		} else if (eventKind.equals(EVENT_KIND_BEHAVIOUR)) {
			TruckDrivingInfo truckDrivingInfo = TruckDrivingInfo.newBuilder()
					.setTimestamp(new Date().getTime() * timeFactor)
					.setTruckId(truck.getTruckId())
					.setDriverId(truck.getDriver().getDriverId())
					.setEventType(this.eventType.toString())
					.setRouteId(truck.getDriver().getRoute().getRouteId())
					.setCorrelationId(correlationId).build();
			return truckDrivingInfo;
		}
		return null;
	}

	public MobileEyeEvent(long correlationId, Location location, MobileEyeEventTypeEnum eventType,
			Truck truck) {
		this.location = location;
		this.eventType = eventType;
		this.truck = truck;
		this.correlationId = correlationId;
	}

	public MobileEyeEventTypeEnum getEventType() {
		return eventType;
	}

	public void setEventType(MobileEyeEventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public Location getLocation() {
		return location;
	}
	
	public Truck getTruck() {
		return this.truck;
	}

	@Override
	public String toString() {
		return truck.toString() + eventType.toString() + ","
				+ location.getLatitude() + "," + location.getLongitude() + "," + correlationId;
	}

	public String toCSV(Integer eventSchema, Integer eventKind, String timeResoultion) {
		if (eventSchema.equals(EVENT_SCHEMA_1) )
			return toCSV1(eventKind, timeResoultion);
		else if (eventSchema.equals(EVENT_SCHEMA_2) )
			return toCSV2(eventKind, timeResoultion);
		else
			throw new RuntimeException("Invalid eventSchema: " + eventSchema);
	}

	public String toJSON(Integer eventSchema, Integer eventKind, String timeResoultion) {
		if (eventSchema.equals(EVENT_SCHEMA_1) )
			return toJSON1(eventKind, timeResoultion);
		else if (eventSchema.equals(EVENT_SCHEMA_2) )
			return toJSON2(eventKind, timeResoultion);
		else
			throw new RuntimeException("Invalid eventSchema: " + eventSchema);
	}

	public Object toAVRO(Integer eventSchema, Integer eventKind, String timeResoultion) {
		if (eventSchema.equals(EVENT_SCHEMA_1) )
			return toAVRO1(eventKind, timeResoultion);
//		else if (eventSchema.equals(EVENT_SCHEMA_2) )
//			return toAVRO2(eventKind, timeResoultion);
		else
			throw new RuntimeException("Invalid eventSchema: " + eventSchema);
	}

}
