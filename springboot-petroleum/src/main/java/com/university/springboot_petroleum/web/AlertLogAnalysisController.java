package com.university.springboot_petroleum.web;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.springboot_petroleum.dto.AlarmDeviceTypeCount;
import com.university.springboot_petroleum.dto.AlarmOrderCount;
import com.university.springboot_petroleum.dto.AlarmSensorCount;
import com.university.springboot_petroleum.dto.AlarmSeverityCountToday;
import com.university.springboot_petroleum.dto.AlarmSeverityWeekDaily;
import com.university.springboot_petroleum.dto.AlarmStatusCount;
import com.university.springboot_petroleum.dto.AlarmWeekCountDTO;
import com.university.springboot_petroleum.exception.DataEmptyException;
import com.university.springboot_petroleum.service.AlarmLogAnalysisService;
import com.university.springboot_petroleum.vo.AlarmLogWeekCountVo;
import com.university.springboot_petroleum.vo.DailySeverityCountVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AlertLogAnalysisController {
 
	@Autowired
	private AlarmLogAnalysisService analysisService;
	//最近一小时告警数量
	@GetMapping(value = "/api/alarmlog/analysis/hourCount")
	public int qryAlarmLogCountHour() {
		log.info("/api/alarmlog/analysis/hourCount enter");
		int count = analysisService.qryAlarmLogCountHour();
		return count;
	}
	//今日小时告警数量 小时-数量
	@GetMapping(value = "/api/alarmlog/analysis/hoursCount")
	public AlarmLogWeekCountVo qryAlarmLogCountHours() {
		log.info("/api/alarmlog/analysis/hoursCount enter");
		List<AlarmWeekCountDTO> awList = analysisService.qryAlarmLogCountHours();
		if(awList != null && (awList.isEmpty() == false)) {
			int size = awList.size();
			List<String> date = new ArrayList<>();
			List<Integer> count = new ArrayList<>();
			for(int i = 0; i < size; i++) {
				AlarmWeekCountDTO aw = awList.get(i);
				date.add(aw.getDate());
				count.add(aw.getCount());
			}
			AlarmLogWeekCountVo awVo = new AlarmLogWeekCountVo(date, count);
			return awVo;
		}else {
			throw new DataEmptyException("no alarm log week data info");
		}
	}
	
	//今日告警数量
	@GetMapping(value = "/api/alarmlog/analysis/todayCount")
	public int qryAlarmLogCountToday() {
		log.info("qryAlarmLogCountToday enter");
		int count = analysisService.qryAlarmLogCountToday();
		return count;
	}
	//最近七天告警数量 天-数量
	@GetMapping(value = "/api/alarmlog/analysis/weekCount")
	public AlarmLogWeekCountVo qryAlarmLogCountWeek(){
		log.info("qryAlarmLogCountWeek enter");
		List<AlarmWeekCountDTO> awList = analysisService.qryAlarmLogCountWeek();
		if(awList != null && (awList.isEmpty() == false)) {
			int size = awList.size();
			List<String> date = new ArrayList<>();
			List<Integer> count = new ArrayList<>();
			for(int i = 0; i < size; i++) {
				AlarmWeekCountDTO aw = awList.get(i);
				date.add(aw.getDate());
				count.add(aw.getCount());
			}
			AlarmLogWeekCountVo awVo = new AlarmLogWeekCountVo(date, count);
			return awVo;
		}else {
			throw new DataEmptyException("no alarm log week data info");
		}
	}
	//今日告警级别占比分析
	@GetMapping(value = "/api/alarmlog/analysis/todaySeverity")
	public List<AlarmSeverityCountToday> qrySeverityCountToday() {
		log.info("");
		List<AlarmSeverityCountToday> result = analysisService.qrySeverityCountToday();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log severity data info");
		}
		return result;
	}
	//今日告警状态占比分析
	@GetMapping(value = "/api/alarmlog/analysis/todayStatus")
	public List<AlarmStatusCount> qryStatusCountToday(){
		log.info("");
		List<AlarmStatusCount> result = analysisService.qryStatusCountToday();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	//当天每小时告警级别数量趋势图
	@GetMapping(value = "/api/alarmlog/analysis/hourSeverityDaily")
	public DailySeverityCountVo qrySeverityHoursCountDaily(){
		List<AlarmSeverityWeekDaily> result = analysisService.qrySeverityHourDaily();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		int size = result.size();
		Map<String, Integer> dateMap = new HashMap<>();
		Map<String, List<Integer>> severityMap = new HashMap<>();
		List<String> dateList = new ArrayList<>();
		
		for(int i = 0; i < size; i++) {
			AlarmSeverityWeekDaily aswd = result.get(i);
			String date = aswd.getDate();
			String severity = aswd.getSeverity();
			int count = aswd.getCount();
			Integer value = dateMap.get(date);
			if(value == null) {
				dateList.add(date);
				dateMap.put(date, 1);
			}
			
			List<Integer> countList = severityMap.get(severity);
			if(countList == null) {
				countList = new ArrayList<>();
				countList.add(count);
			}else {
				countList.add(count);
			}
			severityMap.put(severity, countList);
		}
		
		DailySeverityCountVo dscVo = new DailySeverityCountVo(dateList, severityMap);
		return dscVo;
	}
	//七天告警级别数量趋势图分析
	@GetMapping(value = "/api/alarmlog/analysis/weekSeverityDaily")
	public DailySeverityCountVo qrySeverityWeekCountDaily(){
		List<AlarmSeverityWeekDaily> result = analysisService.qrySeverityWeekDaily();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		int size = result.size();
		Map<String, Integer> dateMap = new HashMap<>();
		Map<String, List<Integer>> severityMap = new HashMap<>();
		List<String> dateList = new ArrayList<>();
		
		for(int i = 0; i < size; i++) {
			AlarmSeverityWeekDaily aswd = result.get(i);
			String date = aswd.getDate();
			String severity = aswd.getSeverity();
			int count = aswd.getCount();
			Integer value = dateMap.get(date);
			if(value == null) {
				dateList.add(date);
				dateMap.put(date, 1);
			}
			
			List<Integer> countList = severityMap.get(severity);
			if(countList == null) {
				countList = new ArrayList<>();
				countList.add(count);
			}else {
				countList.add(count);
			}
			severityMap.put(severity, countList);
		}
		
		DailySeverityCountVo dscVo = new DailySeverityCountVo(dateList, severityMap);
		return dscVo;
	}
	//过去七天告警级别占比分析
	@GetMapping(value = "/api/alarmlog/analysis/weekSeverity")
	public List<AlarmSeverityCountToday> qrySeverityWeekCount(){
		List<AlarmSeverityCountToday> result = analysisService.qrySeverityWeekCount();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	//过去七天告警状态占比分析
	@GetMapping(value = "/api/alarmlog/analysis/weekStatus")
	public List<AlarmStatusCount> qryStatusWeekCount(){
		List<AlarmStatusCount> result = analysisService.qryStatusWeekCount();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	

	//过去7天告警产生设备分析
	@GetMapping(value = "/api/alarmlog/analysis/weekSensor")
	public List<AlarmSensorCount> qrySensorCountWeek(){
		List<AlarmSensorCount> result = analysisService.qrySensorNameWeekCount();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	//过去7天告警设备类型分析
	@GetMapping(value = "/api/alarmlog/analysis/weekDeviceType")
	public List<AlarmDeviceTypeCount> qryDeviceTypeCountWeek(){
		List<AlarmDeviceTypeCount> result = analysisService.qryDeviceTypeWeekCount();
		if(result == null||result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	
	//今日top告警内容分析
	@GetMapping(value = "/api/alarmlog/analysis/todayTop")
	public List<AlarmOrderCount> qryTopAlarmIDToday(){
		List<AlarmOrderCount> result = analysisService.qryTopAlarmIDToday();
		if(result == null || result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	//7天top告警内容分析
	@GetMapping(value = "/api/alarmlog/analysis/weekTop")
	public List<AlarmOrderCount> qryTopAlarmIDWeek(){
		List<AlarmOrderCount> result = analysisService.qryTopAlarmIDWeekCount();
		if(result == null || result.isEmpty()) {
			throw new DataEmptyException("no alarm log status data info");
		}
		return result;
	}
	
	
}
