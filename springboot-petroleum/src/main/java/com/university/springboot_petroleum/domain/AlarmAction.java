package com.university.springboot_petroleum.domain;

import lombok.Data;

@Data
public class AlarmAction {

	private String actionType;
	private String actionObject;
	private String operation;
	private String content;
	
}
