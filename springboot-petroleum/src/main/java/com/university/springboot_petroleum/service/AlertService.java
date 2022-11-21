package com.university.springboot_petroleum.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.domain.AlarmAction;


import com.university.springboot_petroleum.domain.AlarmSeverity;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.vo.AlarmActionTypeVo;

@Service
public interface AlertService {

	public List<AlarmSeverity> qryAlarmSeverity();	
	
	public List<AlarmActionTypeVo> qryActionType();
	
	public void addAlarmRule(String alarmName,String alarmLevel,String condition,List<AlarmConditionDTO> conditions,List<AlarmAction> actions);
	
	public List<AlarmMainInfoDTO> qryAlarmMainInfo();
	
	public AlarmMainInfoDTO qryAlarmMainInfoByID(int alarmID);
	
	public List<AlarmConditionDTO> qryAlarmConditionByID(int alarmID);
	
	public List<AlarmActionDTO> qryAlarmActionByID(int alarmID);
	
	public List<AlarmMainInfoDTO> qryRunningAlarmMainInfo();
	
	public Map<Integer,List<AlarmConditionDTO>> qryAlarmCondition();
	
	public Map<Integer,List<AlarmActionDTO>> qryAlarmAction();
	
	public boolean delAlaramRuleByID(int alarmID);
	
	public boolean updateAlarmStatus(int alarmID,int status);
}
