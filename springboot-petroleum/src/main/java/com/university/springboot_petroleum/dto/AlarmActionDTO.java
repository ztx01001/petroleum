package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmActionDTO {
	private int actionID;
	private int alarmID;
	private String actionType;
	private String actionObject;
	private String operation;
	private String content;
	public AlarmActionDTO(int actionID,int alarmID,String actionType,String actionObject,String operation,String content) {
	    this.actionID = actionID;
	    this.alarmID = alarmID;
	    this.actionType = actionType;
	    this.actionObject = actionObject;
	    this.operation = operation;
	    this.content = content;		
	}

}
