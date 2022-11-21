package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmOrderCount {
	private int orderID;
	private int alarmID;
	private String alarmName;
	private int count;
	public AlarmOrderCount(int orderID,int alarmID,String alarmName,int count) {
		this.orderID = orderID;
		this.alarmID = alarmID;
		this.alarmName = alarmName;
		this.count = count;
	}

}
