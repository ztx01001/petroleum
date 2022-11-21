package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmConditionDTO {
	private int conditionID;
	private int alarmID;
	private String deviceType;
	private int sensorID;
	private String sensorName;
	private String operator;
	private float thresholdValue;
	private String relationValue; 
	
	public AlarmConditionDTO(String deviceType,int sensorID,String sensorName,String opertor,float thresholdValue,String relationValue) {
		this.deviceType = deviceType;
		this.sensorID = sensorID;
		this.sensorName = sensorName;
		this.operator = opertor;
		this.thresholdValue = thresholdValue;
		this.relationValue = relationValue;
	}
	
	public AlarmConditionDTO(int conditionID,int alarmID,String deviceType,int sensorID,String sensorName,String operator,float thresholdValue,String relationValue) {
		this.conditionID = conditionID;
		this.alarmID = alarmID;
		this.deviceType = deviceType;
		this.sensorID = sensorID;
		this.sensorName = sensorName;
		this.operator = operator;
		this.thresholdValue = thresholdValue;
		this.relationValue = relationValue;
	}
	

}
