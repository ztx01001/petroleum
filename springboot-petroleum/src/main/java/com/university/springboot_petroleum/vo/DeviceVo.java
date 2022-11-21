package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class DeviceVo {

	private String label;
	private int value;
	
	public DeviceVo(String label,int value) {
		this.label = label;
		this.value = value;
	}
}
