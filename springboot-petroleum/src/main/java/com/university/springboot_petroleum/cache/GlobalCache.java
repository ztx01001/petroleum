package com.university.springboot_petroleum.cache;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.service.AlertService;
import com.university.springboot_petroleum.service.SensorInfoService;

@Component
@Order(1)
public class GlobalCache {
	private static Logger log = LoggerFactory.getLogger(GlobalCache.class);

	//key=sensorID value=sensorInfo
	public static Map<Integer,SensorInfo> SensorInfoMap = new ConcurrentHashMap<>();
	//key=attributeName value=list<sensorInfo>
	public static Map<String, List<SensorInfo>> AttrSensorMap  = new ConcurrentHashMap<>();
	
	
	public static List<AlarmMainInfoDTO> alertArray = new ArrayList<>();
	
	public static List<AlarmMainInfoDTO> AlarmMainInfoList = Collections.synchronizedList(alertArray);
	
	public static Map<Integer, List<AlarmConditionDTO>> AlarmConditionMap = new ConcurrentHashMap<>();
	
	public static Map<Integer, List<AlarmActionDTO>>  AlarmActionMap = new ConcurrentHashMap<>();
	
	@Autowired
	SensorInfoService sensorImpl;
	
	@Autowired
	AlertService alertServiceImpl;
	
	@PostConstruct
	public void init() {
		log.info("GlobalCache init start---");
		Map<Integer,SensorInfo> sensorMap = sensorImpl.qrySensorInfoMap();
		for(Map.Entry<Integer, SensorInfo> entry:sensorMap.entrySet()) {
			SensorInfo sensorInfo = entry.getValue();
			SensorInfoMap.put(entry.getKey(), sensorInfo);
			String attributeName = sensorInfo.getAttributeName();
			List<SensorInfo> sensorList = AttrSensorMap.get(attributeName);
			if(sensorList == null || sensorList.isEmpty()) {
				sensorList = new ArrayList<>();
				sensorList.add(sensorInfo);
				AttrSensorMap.put(attributeName, sensorList);
			}else {
				sensorList.add(sensorInfo);
				AttrSensorMap.put(attributeName, sensorList);
			}
		}
		
		List<AlarmMainInfoDTO> alarmMainInfos = alertServiceImpl.qryRunningAlarmMainInfo();
		if(alarmMainInfos != null && ( alarmMainInfos.isEmpty() == false)) {
			int size = alarmMainInfos.size();
			for(int i = 0; i < size; i++) {
				AlarmMainInfoList.add(alarmMainInfos.get(i));
			}
		}
		Map<Integer, List<AlarmConditionDTO>> conditionMap = alertServiceImpl.qryAlarmCondition();
		if(conditionMap != null && (conditionMap.size() > 0)) {
			for(Map.Entry<Integer, List<AlarmConditionDTO>> entry:conditionMap.entrySet()) {
				AlarmConditionMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		Map<Integer, List<AlarmActionDTO>> actionMap = alertServiceImpl.qryAlarmAction();
		if(actionMap != null && (actionMap.size() > 0)) {
			for(Map.Entry<Integer, List<AlarmActionDTO>> entry:actionMap.entrySet()) {
				AlarmActionMap.put(entry.getKey(), entry.getValue());
			}
			
		}
		
		log.info("GlobalCache init end---");
	}
	
	@PreDestroy
	public void destroy() {
		
	}
}
