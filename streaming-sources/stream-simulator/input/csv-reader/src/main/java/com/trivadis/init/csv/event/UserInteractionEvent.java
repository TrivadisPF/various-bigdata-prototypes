package com.trivadis.init.csv.event;

public class UserInteractionEvent extends AbstractEvent {
	
	private String vehicleCode;
	private int odometer;
	private int actualTime;
	private int nominalTime;
	private int interactionType;
	private String value1;
	private String value2;
	private String value3;
	private String value4;
	private String value5;

	public String getVehicleCode() {
		return vehicleCode;
	}

	public int getOdometer() {
		return odometer;
	}

	public int getActualTime() {
		return actualTime;
	}

	public int getNominalTime() {
		return nominalTime;
	}

	public int getInteractionType() {
		return interactionType;
	}

	public String getValue1() {
		return value1;
	}

	public String getValue2() {
		return value2;
	}

	public String getValue3() {
		return value3;
	}

	public String getValue4() {
		return value4;
	}

	public String getValue5() {
		return value5;
	}

	public UserInteractionEvent(String vehicleCode, int odometer, int actualTime, int nominalTime, int interactionType,
			String value1, String value2, String value3, String value4, String value5) {
		super();
		this.vehicleCode = vehicleCode;
		this.odometer = odometer;
		this.actualTime = actualTime;
		this.nominalTime = nominalTime;
		this.interactionType = interactionType;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
	}

	@Override
	public Integer getEventTimestamp() {
		return this.actualTime;
	}
	
	@Override
	public String toString() {
		return "UserInteraction [vehicleCode=" + vehicleCode + ", odometer=" + odometer + ", actualTime="
				+ actualTime + ", nominalTime=" + nominalTime + ", interactionType=" + interactionType + ", value1="
				+ value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + ", value5=" + value5
				+ "]";
	}

	
}
