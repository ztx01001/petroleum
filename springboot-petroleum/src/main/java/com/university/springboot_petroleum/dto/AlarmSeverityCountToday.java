package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmSeverityCountToday {

	private String severity;
	private int count;
	
	public AlarmSeverityCountToday(String severity,int count) {
		this.severity = severity;
		this.count = count;
	}
}
