package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmStatusCount {

	private int statusID;
	private String statusValue;
	private String displayName;
	private int count;
	
	public AlarmStatusCount(int statusID,String statusValue,int count) {
		this.statusID = statusID;
		this.statusValue = statusValue;
		this.count = count;
	}
	
	public AlarmStatusCount(int statusID,String statusValue,String displayName,int  count) {
		this.statusID = statusID;
		this.statusValue = statusValue;
		this.displayName = displayName;
		this.count = count;
	}
}
