package com.university.springboot_petroleum.domain;

import lombok.Data;

@Data
public class AlarmCondition {
	private String deviceType;
	private int deviceID;
	private String operator;
	private float thresholdValue;
	private String relationValue;
	
	

}
