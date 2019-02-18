package com.trivadis.init.csv.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PointCode extends AbstractEvent {

	private String code;
	private String shortName;
	private String longName;
	private Double longitude;
	private Double latitude;
	private String pointType;
	private String districtCode;
	private String stopIdentifier;
	private Integer positionNo;
	private String organisationalUnit;
	private String municipalty;
	private String streetDirection;
	private String street;
	private String streetCrossing;
	private String intersectionPosition;
	private String sheltered;
	private String barrierFree;

	public String getCode() {
		return code;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public String getPointType() {
		return pointType;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public String getStopIdentifier() {
		return stopIdentifier;
	}

	public Integer getPositionNo() {
		return positionNo;
	}

	public String getOrganisationalUnit() {
		return organisationalUnit;
	}

	public String getMunicipalty() {
		return municipalty;
	}

	public String getStreetDirection() {
		return streetDirection;
	}

	public String getStreet() {
		return street;
	}

	public String getStreetCrossing() {
		return streetCrossing;
	}

	public String getIntersectionPosition() {
		return intersectionPosition;
	}

	public String getSheltered() {
		return sheltered;
	}

	public String getBarrierFree() {
		return barrierFree;
	}

	public PointCode(String code, String shortName, String longName, Double longitude, Double latitude,
			String pointType, String districtCode, String stopIdentifier, Integer positionNo, String organisationalUnit,
			String municipalty, String streetDirection, String street, String streetCrossing,
			String intersectionPosition, String sheltered, String barrierFree) {
		super();
		this.code = code;
		this.shortName = shortName;
		this.longName = longName;
		this.longitude = longitude;
		this.latitude = latitude;
		this.pointType = pointType;
		this.districtCode = districtCode;
		this.stopIdentifier = stopIdentifier;
		this.positionNo = positionNo;
		this.organisationalUnit = organisationalUnit;
		this.municipalty = municipalty;
		this.streetDirection = streetDirection;
		this.street = street;
		this.streetCrossing = streetCrossing;
		this.intersectionPosition = intersectionPosition;
		this.sheltered = sheltered;
		this.barrierFree = barrierFree;
	}

	@Override
	public Integer getEventTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "PointCode [code=" + code + ", shortName=" + shortName + ", longName=" + longName + ", longitude="
				+ longitude + ", latitude=" + latitude + ", pointType=" + pointType + ", districtCode=" + districtCode
				+ ", stopIdentifier=" + stopIdentifier + ", positionNo=" + positionNo + ", organisationalUnit="
				+ organisationalUnit + ", municipalty=" + municipalty + ", streetDirection=" + streetDirection
				+ ", street=" + street + ", streetCrossing=" + streetCrossing + ", intersectionPosition="
				+ intersectionPosition + ", sheltered=" + sheltered + ", barrierFree=" + barrierFree + "]";
	}
	
 	public String toJson() throws JsonProcessingException {
 		ObjectMapper mapper = new ObjectMapper();
 		
 		return mapper.writeValueAsString(this);
 	}
 	
 	

}
