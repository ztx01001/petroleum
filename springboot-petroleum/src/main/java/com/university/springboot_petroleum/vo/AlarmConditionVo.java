package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class AlarmConditionVo {

	private String deviceType;
	private int deviceID;
	private String deviceName;
	private String operator;
	private float thresholdValue;
	private String relationValue;
	
	public AlarmConditionVo(String deviceType,int deviceID,String deviceName,String operator,float thresholdValue,String relationValue) {
		this.deviceType = deviceType;
		this.deviceID = deviceID;
		this.deviceName = deviceName;
		this.operator = operator;
		this.thresholdValue = thresholdValue;
		this.relationValue = relationValue;
	}
}
