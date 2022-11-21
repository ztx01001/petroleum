package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmSensorCount {

	private String sensorName;
	private int count;
	public AlarmSensorCount(String sensorName,int count) {
		this.sensorName = sensorName;
		this.count = count;
	}
}
