package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class SeverityVo {
	private String label;
	private int value;
	private String disName;
	
	public SeverityVo(String label,int value,String disName) {
		this.label = label;
		this.value = value;
		this.disName = disName;
	}

}
