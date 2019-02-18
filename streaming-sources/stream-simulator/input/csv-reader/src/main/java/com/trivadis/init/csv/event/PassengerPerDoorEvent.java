package com.trivadis.init.csv.event;

public class PassengerPerDoorEvent extends AbstractEvent {
	private String vehicleCode;
	private Integer odometer;
	private Integer actualTime;
	private Integer nominalArrivalTime;
	private Integer nominalDepartureTime;
	private String stopCode;
	private Integer positionNo;
	private Integer wagonNo;
	private Integer doorNo;
	private Integer boardings;
	private Integer alightings;
	public String getVehicleCode() {
		return vehicleCode;
	}
	public Integer getOdometer() {
		return odometer;
	}
	public Integer getActualTime() {
		return actualTime;
	}
	public Integer getNominalArrivalTime() {
		return nominalArrivalTime;
	}
	public Integer getNominalDepartureTime() {
		return nominalDepartureTime;
	}
	public String getStopCode() {
		return stopCode;
	}
	public Integer getPositionNo() {
		return positionNo;
	}
	public Integer getWagonNo() {
		return wagonNo;
	}
	public Integer getDoorNo() {
		return doorNo;
	}
	public Integer getBoardings() {
		return boardings;
	}
	public Integer getAlightings() {
		return alightings;
	}
	
	@Override
	public Integer getEventTimestamp() {
		return actualTime;
	}
	
	public PassengerPerDoorEvent(String vehicleCode, Integer odometer, Integer actualTime, Integer nominalArrivalTime,
			Integer nominalDepartureTime, String stopCode, Integer positionNo, Integer wagonNo, Integer doorNo,
			Integer boardings, Integer alightings) {
		super();
		this.vehicleCode = vehicleCode;
		this.odometer = odometer;
		this.actualTime = actualTime;
		this.nominalArrivalTime = nominalArrivalTime;
		this.nominalDepartureTime = nominalDepartureTime;
		this.stopCode = stopCode;
		this.positionNo = positionNo;
		this.wagonNo = wagonNo;
		this.doorNo = doorNo;
		this.boardings = boardings;
		this.alightings = alightings;
	}
	@Override
	public String toString() {
		return "PassengerPerDoorEvent [vehicleCode=" + vehicleCode + ", odometer=" + odometer + ", actualTime="
				+ actualTime + ", nominalArrivalTime=" + nominalArrivalTime + ", nominalDepartureTime="
				+ nominalDepartureTime + ", stopCode=" + stopCode + ", positionNo=" + positionNo + ", wagonNo=" + wagonNo
				+ ", doorNo=" + doorNo + ", boardings=" + boardings + ", alightings=" + alightings + "]";
	}

}
