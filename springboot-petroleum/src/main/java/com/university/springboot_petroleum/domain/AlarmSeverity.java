package com.university.springboot_petroleum.domain;

import lombok.Data;

@Data
public class AlarmSeverity {
	private int id;
	private String name;
	private String chinese;
	private int value;
	
	public AlarmSeverity(int id,String name,String chinese,int value) {
		this.id = id;
		this.name = name;
		this.chinese = chinese;
		this.value = value;
	}

}
