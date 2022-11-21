package com.university.springboot_petroleum.vo;

import java.util.List;

import com.university.springboot_petroleum.domain.AlarmAction;


import lombok.Data;

@Data
public class AlarmDetInfoVo {
	private String alarmName;
	private String alarmLevel;
	List<AlarmConditionVo> conditionsCard;
	List<AlarmAction> actionsCard;
	
	
	

}
