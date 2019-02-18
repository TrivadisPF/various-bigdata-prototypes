package com.trivadis.init.csv.event;

public class CyclicMessageEvent extends AbstractEvent {
	private String vehicleCode;
	private Integer odometer;
	private Integer actualTime;
	private Integer velocity;
	private Integer direction;
	private Double actualGPSLongitude;
	private Double actualGPSLatitude;
	private Double actualGPSAltitude;
	private Integer actualGPSSatelites;
	private Integer actualGPSQuality;
	private Double actualGPSHDOP;
	private Double mapGPSLongitude;
	private Double mapGPSLatitude;
	private Double mapGPSAltitude;
	private String radioType;
	private Integer singalStrength;
	private Integer bitErrorRate;
	private Integer coverage;
	private Integer provider;
	private Integer signalToNoiseRatio;
	private Integer scheduleDeviation;
	private String baseStation;

	public String getVehicleCode() {
		return vehicleCode;
	}

	public Integer getOdometer() {
		return odometer;
	}

	public Integer getActualTime() {
		return actualTime;
	}

	public Integer getVelocity() {
		return velocity;
	}

	public Integer getDirection() {
		return direction;
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

	public Integer getActualGPSSatelites() {
		return actualGPSSatelites;
	}

	public Integer getActualGPSQuality() {
		return actualGPSQuality;
	}

	public Double getActualGPSHDOP() {
		return actualGPSHDOP;
	}

	public Double getMapGPSLongitude() {
		return mapGPSLongitude;
	}

	public Double getMapGPSLatitude() {
		return mapGPSLatitude;
	}

	public Double getMapGPSAltitude() {
		return mapGPSAltitude;
	}

	public String getRadioType() {
		return radioType;
	}

	public Integer getSingalStrength() {
		return singalStrength;
	}

	public Integer getBitErrorRate() {
		return bitErrorRate;
	}

	public Integer getCoverage() {
		return coverage;
	}

	public Integer getProvider() {
		return provider;
	}

	public Integer getSignalToNoiseRatio() {
		return signalToNoiseRatio;
	}

	public Integer getScheduleDeviation() {
		return scheduleDeviation;
	}

	public String getBaseStation() {
		return baseStation;
	}
	
	public CyclicMessageEvent(String vehicleCode, Integer odometer, Integer actualTime, Integer velocity, Integer direction,
			Double actualGPSLongitude, Double actualGPSLatitude, Double actualGPSAltitude, Integer actualGPSSatelites,
			Integer actualGPSQuality, Double actualGPSHDOP, Double mapGPSLongitude, Double mapGPSLatitude,
			Double mapGPSAltitude, String radioType, Integer singalStrength, Integer bitErrorRate, Integer coverage,
			Integer provider, Integer signalToNoiseRatio, Integer scheduleDeviation, String baseStation) {
		super();
		this.vehicleCode = vehicleCode;
		this.odometer = odometer;
		this.actualTime = actualTime;
		this.velocity = velocity;
		this.direction = direction;
		this.actualGPSLongitude = actualGPSLongitude;
		this.actualGPSLatitude = actualGPSLatitude;
		this.actualGPSAltitude = actualGPSAltitude;
		this.actualGPSSatelites = actualGPSSatelites;
		this.actualGPSQuality = actualGPSQuality;
		this.actualGPSHDOP = actualGPSHDOP;
		this.mapGPSLongitude = mapGPSLongitude;
		this.mapGPSLatitude = mapGPSLatitude;
		this.mapGPSAltitude = mapGPSAltitude;
		this.radioType = radioType;
		this.singalStrength = singalStrength;
		this.bitErrorRate = bitErrorRate;
		this.coverage = coverage;
		this.provider = provider;
		this.signalToNoiseRatio = signalToNoiseRatio;
		this.scheduleDeviation = scheduleDeviation;
		this.baseStation = baseStation;
	}

	@Override
	public Integer getEventTimestamp() {
		return this.actualTime;
	}

	@Override
	public String toString() {
		return "CyclicMessage [vehicleCode=" + vehicleCode + ", odometer=" + odometer + ", actualTime=" + actualTime
				+ ", velocity=" + velocity + ", direction=" + direction + ", actualGPSLongitude=" + actualGPSLongitude
				+ ", actualGPSLatitude=" + actualGPSLatitude + ", actualGPSAltitude=" + actualGPSAltitude
				+ ", actualGPSSatelites=" + actualGPSSatelites + ", actualGPSQuality=" + actualGPSQuality
				+ ", actualGPSHDOP=" + actualGPSHDOP + ", mapGPSLongitude=" + mapGPSLongitude + ", mapGPSLatitude="
				+ mapGPSLatitude + ", mapGPSAltitude=" + mapGPSAltitude + ", radioType=" + radioType
				+ ", singalStrength=" + singalStrength + ", bitErrorRate=" + bitErrorRate + ", coverage=" + coverage
				+ ", provider=" + provider + ", signalToNoiseRatio=" + signalToNoiseRatio + ", scheduleDeviation="
				+ scheduleDeviation + ", baseStation=" + baseStation + "]";
	}	
	
	
}
