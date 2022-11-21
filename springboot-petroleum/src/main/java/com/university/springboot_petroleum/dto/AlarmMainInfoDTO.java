package com.university.springboot_petroleum.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AlarmMainInfoDTO {
	private int alarmID;
	private String alarmName;
	private String alarmLevel;
	private String condition;
	private Timestamp createTime;
	private int status;
	
	public AlarmMainInfoDTO(int alarmID,String alarmName,String alarmLevel,String condition,Timestamp createTime,int status ) {
		this.alarmID = alarmID;
		this.alarmName = alarmName;
		this.alarmLevel = alarmLevel;
		this.condition = condition;
		this.createTime = createTime;
		this.status = status;
	}

}
