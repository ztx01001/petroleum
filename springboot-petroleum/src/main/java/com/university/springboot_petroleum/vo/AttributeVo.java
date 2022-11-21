package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class AttributeVo {
	private String label;
	private String value;
	
	public AttributeVo(String label,String value) {
		this.label = label;
		this.value = value;
	}

}
