package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class AlarmActionTypeVo {
	private String label;
	private String value;
	public AlarmActionTypeVo(String label,String value) {
		this.label = label;
		this.value = value;
	}

}
