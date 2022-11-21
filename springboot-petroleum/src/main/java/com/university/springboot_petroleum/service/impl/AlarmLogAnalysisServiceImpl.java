package com.university.springboot_petroleum.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.dto.AlarmDeviceTypeCount;
import com.university.springboot_petroleum.dto.AlarmOrderCount;
import com.university.springboot_petroleum.dto.AlarmSensorCount;
import com.university.springboot_petroleum.dto.AlarmSeverityCountToday;
import com.university.springboot_petroleum.dto.AlarmSeverityWeekDaily;
import com.university.springboot_petroleum.dto.AlarmStatusCount;
import com.university.springboot_petroleum.dto.AlarmWeekCountDTO;
import com.university.springboot_petroleum.service.AlarmLogAnalysisService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AlarmLogAnalysisServiceImpl implements AlarmLogAnalysisService{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	//最近一小时告警数量
	@Override
	public int qryAlarmLogCountHour() {
		int count = 0;
		String sqlString = "SELECT COUNT(*) AS count FROM alarm_log WHERE create_time >= CURRENT_TIMESTAMP - INTERVAL 1 HOUR;";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while(rows.next()) {
				count = rows.getInt("count");
			}
		} catch (Exception e) {
			log.error("");
		}
		return count;
	}
	
	//今天告警数量汇总
	@Override
	public int qryAlarmLogCountToday() {
		int count = 0;
		String sqlString = "select COUNT(*) count from alarm_log where to_days(create_time) = to_days(now());";
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while(rows.next()) {
				count = rows.getInt("count");
			}
		} catch (Exception e) {
			log.error("");
		}
		return count;
	}
	//今天告警级别分析
	@Override
	public List<AlarmSeverityCountToday> qrySeverityCountToday() {
		String sqlString = "select alarm_severity,COUNT(*) count from alarm_log where to_days(create_time) = to_days(now()) GROUP BY alarm_severity;";
		List<AlarmSeverityCountToday> asctList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while(rows.next()) {
				String severity = rows.getString("alarm_severity");
				int count = rows.getInt("count");
				AlarmSeverityCountToday asct = new AlarmSeverityCountToday(severity, count);
				asctList.add(asct);
			}
		} catch (Exception e) {
			log.info("qrySeverityCountToday exception="+e.getLocalizedMessage());
		}
		
		return asctList;
	}
	//今天告警状态分析
	@Override
	public List<AlarmStatusCount> qryStatusCountToday() {
		String sqlString = "select alarm_status,c.alert_status_desc,c.alert_status_remark, COUNT(*) count from alarm_log,alarm_status_config c where alarm_status= c.alert_status_id and to_days(create_time) = to_days(now()) GROUP BY alarm_status;";
		List<AlarmStatusCount> result = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while(rows.next()) {
				int statusID = rows.getInt("alarm_status");
				String statusValue = rows.getString("alert_status_desc");
				String displayName = rows.getString("alert_status_remark");
				int count = rows.getInt("count");
				AlarmStatusCount asc = new AlarmStatusCount(statusID, statusValue, displayName, count);
				result.add(asc);
			}
		} catch (Exception e) {
			log.error("qryStatusCountToday exception="+e.getLocalizedMessage());
		}
		return result;
	}
	//当天每小时告警数量汇总
	@Override
	public List<AlarmWeekCountDTO> qryAlarmLogCountHours(){
		String sql = "SELECT DATE_FORMAT(`create_time`, '%Y-%m-%d %H') AS hours,COUNT(1) AS count FROM alarm_log WHERE DATE(create_time) = CURDATE() GROUP BY hours ORDER BY hours;";
		List<AlarmWeekCountDTO> awList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {				
				String date = rows.getString("hours");
				int count = rows.getInt("count");				
				AlarmWeekCountDTO aw = new AlarmWeekCountDTO(date, count);
				awList.add(aw);
			}
		} catch (Exception e) {
			log.error("qryAlarmLogCountWeek exception="+e.getLocalizedMessage());
		}
		return awList;
	}
	//最近七天告警每日汇总信息
	@Override
	public List<AlarmWeekCountDTO> qryAlarmLogCountWeek() {
		String sql = "SELECT DATE_FORMAT( create_time, '%Y-%m-%d' ) days, count(*) count FROM alarm_log where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time) GROUP BY days;";
		List<AlarmWeekCountDTO> awList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				log.info("qryAlarmLogCountWeek enter");
				String date = rows.getString("days");
				int count = rows.getInt("count");
				log.info("qryAlarmLogCountWeek date="+date+" count="+count);
				AlarmWeekCountDTO aw = new AlarmWeekCountDTO(date, count);
				awList.add(aw);
			}
		} catch (Exception e) {
			log.error("qryAlarmLogCountWeek exception="+e.getLocalizedMessage());
		}
		return awList;
		
	}
	//当天告警级别每小时汇总
	//最近七天每日告警级别汇总
		@Override
		public List<AlarmSeverityWeekDaily> qrySeverityHourDaily() {
			log.info("qrySeverityWeekDaily enter");
			String sql ="SELECT DATE_FORMAT(`create_time`, '%Y-%m-%d %H') AS hours,alarm_severity,COUNT(1) AS count FROM alarm_log WHERE DATE(create_time) = CURDATE() GROUP BY hours,alarm_severity ORDER BY hours;";
			List<AlarmSeverityWeekDaily> asList = new ArrayList<>();
			try {
				SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
				while(rows.next()) {
					String date = rows.getString("hours");
					String severity = rows.getString("alarm_severity");
					int count = rows.getInt("count");
					AlarmSeverityWeekDaily as = new AlarmSeverityWeekDaily(date, severity, count);
					asList.add(as);
				}
			} catch (Exception e) {
				log.error("qrySeverityWeekDaily exception="+e.getLocalizedMessage());
			}
			return asList;
		}
		
	
	//最近七天每日告警级别汇总
	@Override
	public List<AlarmSeverityWeekDaily> qrySeverityWeekDaily() {
		log.info("qrySeverityWeekDaily enter");
		String sql ="SELECT DATE_FORMAT( create_time, '%Y-%m-%d' ) days, alarm_severity,count(*) count FROM alarm_log where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time) GROUP BY days,alarm_severity;";
		List<AlarmSeverityWeekDaily> asList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				String date = rows.getString("days");
				String severity = rows.getString("alarm_severity");
				int count = rows.getInt("count");
				AlarmSeverityWeekDaily as = new AlarmSeverityWeekDaily(date, severity, count);
				asList.add(as);
			}
		} catch (Exception e) {
			log.error("qrySeverityWeekDaily exception="+e.getLocalizedMessage());
		}
		return asList;
	}
	
	//最近七天告警级别分析
	@Override
	public List<AlarmSeverityCountToday> qrySeverityWeekCount() {
		String sql = "SELECT alarm_severity, count(*) count FROM alarm_log where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time) GROUP BY alarm_severity;";
		List<AlarmSeverityCountToday> swList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				String severity = rows.getString("alarm_severity");
				int count = rows.getInt("count");
				AlarmSeverityCountToday as = new AlarmSeverityCountToday(severity, count);
				swList.add(as);
			}
		} catch (Exception e) {
			log.error("qrySeverityWeekCount exception="+e.getLocalizedMessage());
		}
		return swList;
	}
	//过去七天告警状态分析
	@Override
	public List<AlarmStatusCount> qryStatusWeekCount() {
		String sql = "SELECT l.alarm_status,c.alert_status_desc,c.alert_status_remark,count(*) count FROM alarm_log l,alarm_status_config c where l.alarm_status=c.alert_status_id and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(l.create_time) GROUP BY l.alarm_status;";
		List<AlarmStatusCount> ascList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int statusID = rows.getInt("alarm_status");
				String statusValue = rows.getString("alert_status_desc");
				String displayName = rows.getString("alert_status_remark");
				int count = rows.getInt("count");
				AlarmStatusCount asc = new AlarmStatusCount(statusID, statusValue,displayName, count);
				ascList.add(asc);
			}
		} catch (Exception e) {
			log.error("");
		}
		return ascList;
	}
	//最近七天设备类型告警数量分析
	@Override
	public List<AlarmDeviceTypeCount> qryDeviceTypeWeekCount() {
		String sqlString = "SELECT c.device_type,COUNT(*) count FROM alarm_log a,alarm_condition_info c where a.alarm_id=c.alarm_id and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(a.create_time)   GROUP BY c.device_type";
		List<AlarmDeviceTypeCount> adtcList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while (rows.next()) {
				String deviceType = rows.getString("device_type");
				int count = rows.getInt("count");
				AlarmDeviceTypeCount adtc = new AlarmDeviceTypeCount(deviceType, count);
				adtcList.add(adtc);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return adtcList;
	}
	
	//最近七天传感器告警数量分析
	@Override
	public List<AlarmSensorCount> qrySensorNameWeekCount() {
		String sqlString = "SELECT c.sensor_name,COUNT(*) count FROM alarm_log a,alarm_condition_info c where a.alarm_id=c.alarm_id and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(a.create_time)   GROUP BY c.sensor_name";
	    List<AlarmSensorCount> ascList = new ArrayList<>();
	    try {
	    	SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
			while (rows.next()) {
				String sensorName = rows.getString("sensor_name");
				int count = rows.getInt("count");
				AlarmSensorCount asc = new AlarmSensorCount(sensorName, count);
				ascList.add(asc);
			}
		} catch (Exception e) {
			log.error("qrySensorNameWeekCount exception="+e.getLocalizedMessage());
		}
	    return ascList;
	}
	//今天告警内容分析top
	@Override
	public List<AlarmOrderCount> qryTopAlarmIDToday() {
		String sqlString="SELECT alarm_id,alarm_name,COUNT(*) count FROM alarm_log where DATE(create_time) = CURDATE() GROUP BY alarm_id ORDER BY count DESC LIMIT 5;";
	    List<AlarmOrderCount> aocList = new ArrayList<>();
	    try {
	    	SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
	    	int orderID = 0;
			while (rows.next()) {
				orderID++;
				int alarmID = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				int count = rows.getInt("count");
				AlarmOrderCount aoc = new AlarmOrderCount(orderID, alarmID, alarmName, count);
				aocList.add(aoc);
			}
		} catch (Exception e) {
			log.error("qryTopAlarmIDToday exception="+e.getLocalizedMessage());
		}
	    return aocList;
	
	}
	
	//最近七天告警内容分析top
	@Override
	public List<AlarmOrderCount> qryTopAlarmIDWeekCount() {
		String sqlString="SELECT alarm_id,alarm_name,COUNT(*) count FROM alarm_log where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time) GROUP BY alarm_id ORDER BY count DESC LIMIT 5;";
		List<AlarmOrderCount> aocList = new ArrayList<>();
	    try {
	    	SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlString);
	    	int orderID = 0;
			while (rows.next()) {
				orderID++;
				int alarmID = rows.getInt("alarm_id");
				String alarmName = rows.getString("alarm_name");
				int count = rows.getInt("count");
				AlarmOrderCount aoc = new AlarmOrderCount(orderID, alarmID, alarmName, count);
				aocList.add(aoc);
			}
		} catch (Exception e) {
			log.error("qryTopAlarmIDToday exception="+e.getLocalizedMessage());
		}
	    return aocList;
	}
}
