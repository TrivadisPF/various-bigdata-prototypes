package com.trivadis.init.csv.event;

public class HaltEvent extends AbstractEvent {
	private String vehicleCode;
	private Integer odometer;
	private Integer actualArrivalTime;
	private Integer nominalArrivalTime;
	private Integer actualDepartureTime;
	private Integer nominalDepartureTime;
	private String stopCode;
	private Integer positionNo;
	private Integer scheduledDistance;
	private Integer doorCriteria;
	private Integer locationType;
	private Integer stopType;
	private Integer lastStopFlag;
	private Double actualGPSLongitude;
	private Double actualGPSLatitude;
	private Integer patternIndex;
	private String patternPostition;
	private Integer doorOpenTime;
	private String pointCode;
	private String pointRole;
	private String action;
	private String planningType;
//	private String note;
//	private Integer correctionType;

	public String getVehicleCode() {
		return vehicleCode;
	}

	public Integer getOdometer() {
		return odometer;
	}

	public Integer getActualArrivalTime() {
		return actualArrivalTime;
	}

	public Integer getNominalArrivalTime() {
		return nominalArrivalTime;
	}

	public Integer getActualDepartureTime() {
		return actualDepartureTime;
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

	public Integer getScheduledDistance() {
		return scheduledDistance;
	}

	public Integer getDoorCriteria() {
		return doorCriteria;
	}

	public Integer getLocationType() {
		return locationType;
	}

	public Integer getStopType() {
		return stopType;
	}

	public Integer getLastStopFlag() {
		return lastStopFlag;
	}

	public Double getActualGPSLongitude() {
		return actualGPSLongitude;
	}

	public Double getActualGPSLatitude() {
		return actualGPSLatitude;
	}

	public Integer getPatternIndex() {
		return patternIndex;
	}

	public String getPatternPostition() {
		return patternPostition;
	}

	public Integer getDoorOpenTime() {
		return doorOpenTime;
	}

	public String getPointCode() {
		return pointCode;
	}

	public String getPointRole() {
		return pointRole;
	}

	public String getAction() {
		return action;
	}

	public String getPlanningType() {
		return planningType;
	}


	@Override
	public Integer getEventTimestamp() {
		return this.actualArrivalTime;
	}

	public HaltEvent(String vehicleCode, Integer odometer, Integer actualArrivalTime, Integer nominalArrivalTime,
			Integer actualDepartureTime, Integer nominalDepartureTime, String stopCode, Integer positionNo,
			Integer scheduledDistance, Integer doorCriteria, Integer locationType, Integer stopType,
			Integer lastStopFlag, Double actualGPSLongitude, Double actualGPSLatitude, Integer patternIndex,
			String patternPostition, Integer doorOpenTime, String pointCode, String pointRole, String action,
			String planningType) {
		super();
		this.vehicleCode = vehicleCode;
		this.odometer = odometer;
		this.actualArrivalTime = actualArrivalTime;
		this.nominalArrivalTime = nominalArrivalTime;
		this.actualDepartureTime = actualDepartureTime;
		this.nominalDepartureTime = nominalDepartureTime;
		this.stopCode = stopCode;
		this.positionNo = positionNo;
		this.scheduledDistance = scheduledDistance;
		this.doorCriteria = doorCriteria;
		this.locationType = locationType;
		this.stopType = stopType;
		this.lastStopFlag = lastStopFlag;
		this.actualGPSLongitude = actualGPSLongitude;
		this.actualGPSLatitude = actualGPSLatitude;
		this.patternIndex = patternIndex;
		this.patternPostition = patternPostition;
		this.doorOpenTime = doorOpenTime;
		this.pointCode = pointCode;
		this.pointRole = pointRole;
		this.action = action;
		this.planningType = planningType;
	}

	@Override
	public String toString() {
		return "HaltEvent [vehicleCode=" + vehicleCode + ", odometer=" + odometer + ", actualArrivalTime=" + actualArrivalTime
				+ ", nominalArrivalTime=" + nominalArrivalTime + ", actualDepartureTime=" + actualDepartureTime
				+ ", nominalDepartureTime=" + nominalDepartureTime + ", stopCode=" + stopCode + ", positionNo="
				+ positionNo + ", scheduledDistance=" + scheduledDistance + ", DoorCriteria=" + doorCriteria
				+ ", LocationType=" + locationType + ", StopType=" + stopType + ", LastStopFlag=" + lastStopFlag
				+ ", actualGPSLongitude=" + actualGPSLongitude + ", actualGPSLatitude=" + actualGPSLatitude
				+ ", patternIndex=" + patternIndex + ", patternPostition="
				+ patternPostition + ", doorOpenTime=" + doorOpenTime + ", pointCode=" + pointCode + ", pointRole="
				+ pointRole + ", action=" + action + ", planningType=" + planningType + "]";
	}

	
}
