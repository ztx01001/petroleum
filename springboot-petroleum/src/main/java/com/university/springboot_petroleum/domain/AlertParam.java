package com.university.springboot_petroleum.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class AlertParam {

	@NotNull(message = "告警名称不能为空")
	@NotEmpty
	private String name;
	
	@NotNull(message = "告警级别不能为空")
	@NotEmpty
	private String severity;
	
	@NotNull(message = "告警设备不能为空")
	@NotEmpty
	private String device;
	
	@NotNull(message = "告警属性不能为空")
	@NotEmpty
	private String attribute;
	
	@NotNull(message = "告警触发条件不能为空")
	@NotEmpty
	private String condition;
	
	@NotNull(message = "动作类型不能为空")
	@NotEmpty
	private String actionType;
	
	@NotNull(message = "执行对象不能为空")
	@NotEmpty
	private String actionObject;
	
	@NotNull(message = "执行动作不能为空")
	@NotEmpty
	private String operation;
	
	@NotNull(message = "动作内容不能为空")
	@NotEmpty
	private String content;
}
