package com.university.springboot_petroleum.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.springboot_petroleum.cache.GlobalCache;
import com.university.springboot_petroleum.domain.AlarmAction;
import com.university.springboot_petroleum.domain.AlarmCondition;


import com.university.springboot_petroleum.domain.AlarmSeverity;
import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.eventproducer.DisruptorMqService;
import com.university.springboot_petroleum.exception.AlarmRuleException;
import com.university.springboot_petroleum.exception.DataEmptyException;
import com.university.springboot_petroleum.service.AlertService;
import com.university.springboot_petroleum.vo.AlarmActionTypeVo;
import com.university.springboot_petroleum.vo.AlarmConditionVo;
import com.university.springboot_petroleum.vo.AlarmDetInfoVo;
import com.university.springboot_petroleum.vo.AlarmInfoVo;
import com.university.springboot_petroleum.vo.SeverityVo;


@RestController
public class AlertController {
	private static Logger log = LoggerFactory.getLogger(AlertController.class);
	
	@Autowired
	AlertService alertServiceImpl;
	
	@Autowired
    private DisruptorMqService disruptorMqService;
	

	@GetMapping(value = "/api/alarm/severity")	
	public List<SeverityVo> qryAlertSeverity() {
		log.info("AlertController qryAlertSeverity");
		List<AlarmSeverity> result = alertServiceImpl.qryAlarmSeverity();
		if(null == result || result.isEmpty()) {
			throw new DataEmptyException("qry Alert Severity error");
		}else {
			int size = result.size();
			List<SeverityVo> severitys = new ArrayList<>();
			for(int i = 0; i < size; i++) {
				AlarmSeverity aSeverity = result.get(i);
				String labelString = aSeverity.getName();
				int value = aSeverity.getId();
				String disName = aSeverity.getChinese();
				SeverityVo sVo = new SeverityVo(labelString, value,disName);
				severitys.add(sVo);
				
			}
			return severitys;
		}
		
	}
	@GetMapping(value = "/api/alarm/actiontype")	
	public List<AlarmActionTypeVo> qryActionType(){
		List<AlarmActionTypeVo> result = alertServiceImpl.qryActionType();
		if(null == result || result.isEmpty()) {
			throw new DataEmptyException("qry alarm action type error");
		}
		return result;
	}
	
	/*
	 *新增加告警 
	 **/
	@PostMapping(value = "/api/alarm")
	public void addAlarmRule(@RequestBody Map map) {
	
		try {
			String alarmName = String.valueOf(map.get("alarmName"));
			String alarmLevel = String.valueOf(map.get("alarmLevel"));
			List conditonsList = (List)map.get("conditionsCard");
			List actionsList = (List)map.get("actionsCard");
			log.info("alarmName="+alarmName+" ;alarmlevel="+alarmLevel);
			log.info("condition="+conditonsList.get(0));
			log.info("action="+actionsList.get(0));
			ObjectMapper mapper = new ObjectMapper();
			String conditonStr = mapper.writeValueAsString(conditonsList);
			List<AlarmCondition> conditions = mapper.readValue(conditonStr, new TypeReference<List<AlarmCondition>>(){});
			int size = conditions.size();
			StringBuffer conditionsb = new StringBuffer();
			List<AlarmConditionDTO> conditionDTOList = new ArrayList<AlarmConditionDTO>();
			for(int i = 0;i<size;i++) {
				AlarmCondition ac = conditions.get(i);
				String deviceType = ac.getDeviceType();
				int sensorID = ac.getDeviceID();
				String operator = ac.getOperator();
				float  threadholdValue = ac.getThresholdValue();
				String relation = ac.getRelationValue();
				log.info(i+"="+deviceType+"="+sensorID+"="+operator+"="+threadholdValue+"="+relation);
				SensorInfo si = GlobalCache.SensorInfoMap.get(sensorID);
				if(si==null) {
					log.error("can't get sensor info="+sensorID);
					continue;
				}
				String sensorName = si.getName();
				conditionsb.append("(").append(sensorName).append(operator).append(threadholdValue).append(")");
				
				if(relation !=null && (relation.isEmpty() ==false)) {
					conditionsb.append(relation);
				}
				AlarmConditionDTO acd = new AlarmConditionDTO(deviceType, sensorID, sensorName, operator, threadholdValue, relation);
				conditionDTOList.add(acd);
			}
			log.info("condition="+conditionsb.toString());
			String actionStr = mapper.writeValueAsString(actionsList);
			List<AlarmAction> actions = mapper.readValue(actionStr, new TypeReference<List<AlarmAction>>() {});
			alertServiceImpl.addAlarmRule(alarmName, alarmLevel, conditionsb.toString(), conditionDTOList, actions);
			
			disruptorMqService.sendMessage("alarmADD ID-");
		} catch (Exception e) {
			log.error("post /api/alert exception="+e.getLocalizedMessage());
			throw new AlarmRuleException("create alarm rule exception");			
		}
		
		
	}
	/*
	 *查询告警列表，非详细信息 
	 **/
	@GetMapping(value = "/api/alarm")
	public List<AlarmInfoVo> qryAlert() {
		log.info("/api/alarm get enter");
		List<AlarmMainInfoDTO> result = alertServiceImpl.qryAlarmMainInfo();
		if(null == result || result.isEmpty()) {
			throw new DataEmptyException("no alert detail info");
		}
		int size = result.size();
		List<AlarmInfoVo> alertList = new ArrayList<>();
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
		for(int i = 0; i< size; i++) {
			AlarmMainInfoDTO am = result.get(i);
			int alarmID = am.getAlarmID();
			String alarmName = am.getAlarmName();
			String alarmLevel = am.getAlarmLevel();
			String time = sdf.format(am.getCreateTime());
			int status = am.getStatus();
			AlarmInfoVo alertInfoVo = new AlarmInfoVo(alarmID,alarmName,alarmLevel,time,status);
			alertList.add(alertInfoVo);
		}
		return alertList;
	}
	
	/*
	 *查询告警详细信息 
	 **/
	@GetMapping(value = "/api/alarm/")
	public AlarmDetInfoVo qryAlertInfo(@RequestParam("alarmID") int alarmID){
		log.info("/api/alarm/alarmID enter"+alarmID);
		AlarmMainInfoDTO am = alertServiceImpl.qryAlarmMainInfoByID(alarmID);
		
		if(am == null) {
			throw new DataEmptyException("no alert detail info alertID="+alarmID);
		}
		List<AlarmConditionDTO> ac = alertServiceImpl.qryAlarmConditionByID(alarmID);
		List<AlarmActionDTO> aa = alertServiceImpl.qryAlarmActionByID(alarmID);
		AlarmDetInfoVo ad = new AlarmDetInfoVo();
		ad.setAlarmName(am.getAlarmName());
		ad.setAlarmLevel(am.getAlarmLevel());
		if(ac != null && aa != null) {
			int acSize = ac.size();
			int aaSize = aa.size();
			List<AlarmConditionVo> acList = new ArrayList<>();
			for(int i = 0; i < acSize; i++) {
				
				String deviceType = ac.get(i).getDeviceType();
				int deviceID = ac.get(i).getSensorID();
				String deviceName = ac.get(i).getSensorName();
				String operator = ac.get(i).getOperator();
				String relationValue = ac.get(i).getRelationValue();
				float thresholdValue = ac.get(i).getThresholdValue();
				AlarmConditionVo aconditon = new AlarmConditionVo(deviceType, deviceID,deviceName, operator, thresholdValue, relationValue);
				acList.add(aconditon);
			}
			List<AlarmAction> aaList = new ArrayList<>();
			for(int j = 0; j < aaSize; j++) {
				AlarmAction aaction = new AlarmAction();
				aaction.setActionType(aa.get(j).getActionType());
				aaction.setActionObject(aa.get(j).getActionObject());
				aaction.setOperation(aa.get(j).getOperation());
				aaction.setContent(aa.get(j).getContent());
				aaList.add(aaction);
			}
			ad.setConditionsCard(acList);
			ad.setActionsCard(aaList);
		}else {
			throw new DataEmptyException("no alert detail info alertID="+alarmID);
		}
		
		return ad;
	}
	/*
	 * 删除告警
	 **/
	@DeleteMapping(value = "/api/alarm")
	public boolean delAlertByID(@RequestParam("alarmID") int alarmID) {
		log.info("delete /api/alert alertID="+alarmID);
		
		boolean result = alertServiceImpl.delAlaramRuleByID(alarmID);
		if(result ) {
			disruptorMqService.sendMessage("alarmDelete ID-"+alarmID);
		}
		
		return result;
	}
	/**
	 * 更新告警运行状态
	 */
	@PutMapping(value = "/api/alarm")
	public boolean updateAlertStatus(@RequestBody Map params) {
		int alarmID = (Integer)params.get("alarmID");
		int status = (Integer)params.get("status");
		log.info("update /api/alert alarmID="+alarmID+" status="+status);
		boolean result = alertServiceImpl.updateAlarmStatus(alarmID, status);
		if(result) {
			disruptorMqService.sendMessage("alarmUpdate ID-"+alarmID);
		}
		
		return result;
	}

	
}
