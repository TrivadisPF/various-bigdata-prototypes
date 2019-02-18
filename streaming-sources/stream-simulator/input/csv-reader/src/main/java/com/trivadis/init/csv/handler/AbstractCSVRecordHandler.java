package com.trivadis.init.csv.handler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractCSVRecordHandler implements CSVRecordHandler {
	protected Double toDouble(String value) {
		Double returnValue = new Double(0);
		if (!StringUtils.isAllBlank(value)) {
			returnValue = new Double(value);
		}
		return returnValue;
	}
	
	protected Integer toInteger(String value) {
		Integer returnValue = new Integer(0);
		if (!StringUtils.isAllBlank(value)) {
			returnValue = new Integer(value);
		}
		return returnValue;
	}
	
	protected Date toDate(String value) {
		Date returnValue = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		if (!StringUtils.isAllBlank(value)) {
			try {
			  returnValue = formatter.parse(value);
			} catch (ParseException e) {
			  e.printStackTrace();
			}
		}
		return returnValue;
	}
}
