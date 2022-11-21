package com.university.springboot_petroleum.domain;

import lombok.Data;

@Data
public class AlertActionMessage {

	private String type;
	private String object;
	private String act;
	private String content;
	
	public AlertActionMessage(String type,String object,String act,String content) {
		this.type = type;
		this.object = object;
		this.act = act;
		this.content = content;
	}
}
