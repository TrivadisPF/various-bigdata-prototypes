package com.trivadis.init.csv.event;

public class DataRadioVehicleEvent extends AbstractEvent {

	private java.util.Date date;
	private Integer actualTime;
	private Integer radioId;
	private String radioType;
	private Integer messgeType;
	private String messageValue;
	private String ipAddress;
	private Integer deviceOwnerType;
	private Double actualGPSLongitude;
	private Double actualGPSLatitude;
	private Double actualGPSAltitude;
	private String vehicleCode;
	private String lineCode;
	
	public java.util.Date getDate() {
		return date;
	}
	public Integer getActualTime() {
		return actualTime;
	}
	public Integer getRadioId() {
		return radioId;
	}
	public String getRadioType() {
		return radioType;
	}
	public Integer getMessgeType() {
		return messgeType;
	}
	public String getMessageValue() {
		return messageValue;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public Integer getDeviceOwnerType() {
		return deviceOwnerType;
	}
	public Double getActualGPSLongitude() {
		return actualGPSLongitude;
	}
	public Double getActualGPSLatitude() {
		return actualGPSLatitude;
	}
	public Double getActualGPSAltitude() {
		return actualGPSAltitude;
	}
	public String getVehicleCode() {
		return vehicleCode;
	}
	public String getLineCode() {
		return lineCode;
	}
	
	public DataRadioVehicleEvent(java.util.Date date, Integer actualTime, Integer radioId, String radioType,
			Integer messgeType, String messageValue, String ipAddress, Integer deviceOwnerType,
			Double actualGPSLongitude, Double actualGPSLatitude, Double actualGPSAltitude, String vehicleCode,
			String lineCode) {
		super();
		this.date = date;
		this.actualTime = actualTime;
		this.radioId = radioId;
		this.radioType = radioType;
		this.messgeType = messgeType;
		this.messageValue = messageValue;
		this.ipAddress = ipAddress;
		this.deviceOwnerType = deviceOwnerType;
		this.actualGPSLongitude = actualGPSLongitude;
		this.actualGPSLatitude = actualGPSLatitude;
		this.actualGPSAltitude = actualGPSAltitude;
		this.vehicleCode = vehicleCode;
		this.lineCode = lineCode;
	}
	
	@Override
	public Integer getEventTimestamp() {
		return this.actualTime;
	}
	
	@Override
	public String toString() {
		return "DataRadioVehicle [Date=" + date + ", actualTime=" + actualTime + ", radioId=" + radioId
				+ ", radioType=" + radioType + ", messgeType=" + messgeType + ", messageValue=" + messageValue
				+ ", ipAddress=" + ipAddress + ", deviceOwnerType=" + deviceOwnerType + ", actualGPSLongitude="
				+ actualGPSLongitude + ", actualGPSLatitude=" + actualGPSLatitude + ", actualGPSAltitude="
				+ actualGPSAltitude + ", vehicleCode=" + vehicleCode + ", lineCode=" + lineCode + "]";
	}			
	
}

