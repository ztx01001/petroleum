package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmSeverityWeekDaily {

	private String date;
	private String severity;
	private int count;
	public AlarmSeverityWeekDaily(String date,String severity,int count) {
		this.date = date;
		this.severity = severity;
		this.count = count;
	}
}
