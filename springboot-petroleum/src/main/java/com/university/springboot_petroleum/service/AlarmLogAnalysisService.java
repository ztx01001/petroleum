package com.university.springboot_petroleum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.dto.AlarmDeviceTypeCount;
import com.university.springboot_petroleum.dto.AlarmOrderCount;
import com.university.springboot_petroleum.dto.AlarmSensorCount;
import com.university.springboot_petroleum.dto.AlarmSeverityCountToday;
import com.university.springboot_petroleum.dto.AlarmSeverityWeekDaily;
import com.university.springboot_petroleum.dto.AlarmStatusCount;
import com.university.springboot_petroleum.dto.AlarmWeekCountDTO;

@Service
public interface AlarmLogAnalysisService {
	//最近1小时告警数量
	public int qryAlarmLogCountHour() ;
	//当天每个小时告警数量汇总信息
	public List<AlarmWeekCountDTO> qryAlarmLogCountHours();
	//当天每小时告警级别数量汇总
	public List<AlarmSeverityWeekDaily> qrySeverityHourDaily();

	//查询今天告警日志总数
	public int qryAlarmLogCountToday();
	//查询今天告警日志状态分组汇总信息分析
	public List<AlarmStatusCount> qryStatusCountToday() ;
	//查询今天告警日志级别分析，分组汇总信息
	public List<AlarmSeverityCountToday> qrySeverityCountToday();
	//查询最近七天每日告警汇总
	public List<AlarmWeekCountDTO> qryAlarmLogCountWeek();	
	//查询最近七天每日告警级别汇总信息
	public List<AlarmSeverityWeekDaily> qrySeverityWeekDaily();	
	//最近七天告警级别分析
	public List<AlarmSeverityCountToday> qrySeverityWeekCount();
	//最近七天告警状态分析
	public List<AlarmStatusCount> qryStatusWeekCount();
	
	public List<AlarmDeviceTypeCount> qryDeviceTypeWeekCount();
	
	public List<AlarmSensorCount> qrySensorNameWeekCount();
	
	public List<AlarmOrderCount> qryTopAlarmIDToday();
	
	public List<AlarmOrderCount> qryTopAlarmIDWeekCount();
}
