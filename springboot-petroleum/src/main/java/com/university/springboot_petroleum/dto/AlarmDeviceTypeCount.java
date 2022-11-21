package com.university.springboot_petroleum.dto;

import lombok.Data;

@Data
public class AlarmDeviceTypeCount {
	private String deviceType;
	private int count;
	public AlarmDeviceTypeCount(String deviceType,int count) {
		this.deviceType = deviceType;
		this.count = count;
	}

}
