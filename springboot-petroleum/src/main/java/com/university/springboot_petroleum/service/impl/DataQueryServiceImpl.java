package com.university.springboot_petroleum.service.impl;



import java.util.List;
import java.util.Map;

import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.config.InfluxDBConfig;
import com.university.springboot_petroleum.service.DataQueryService;
import com.university.springboot_petroleum.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DataQueryServiceImpl implements DataQueryService{

	@Autowired
	private InfluxDBConfig influxDBConfig;
	
	@Override
	public List<Map<String,Object>> qryData(String start,String end) {
		String startTime = TimeUtil.getUTC(start);
		String endTime = TimeUtil.getUTC(end);
		StringBuffer commandBuf = new StringBuffer();
		commandBuf.append("select \"time\",\"液体流量计1\",\"气体流量计2\",\"温度传感器3\",\"温度传感器4\",\"液位传感器5\",\"压力传感器6\",\"液体流量计7\",\"气体流量计8\",\"温度传感器9\",\"温度传感器10\",\"压力传感器11\",\"液位传感器12\",\"气体流量计13\",\"温度传感器14\",\"压力传感器15\",\"液体流量计16\",\"温度传感器18\",\"压力传感器19\",\"液体流量计20\",\"液位传感器21\",\"温度传感器22\",\"压力传感器23\",\"温度传感器24\",\"压力传感器25\",\"液体流量计26\",\"液位传感器27\",\"压力传感器28\",\"温度传感器29\",\"温度传感器30\",\"压力传感器31\",\"液体流量计32\" from smartOil where time >=");
		commandBuf.append("'"+startTime+"' and time < '");
		commandBuf.append(endTime+"'");
		String command = commandBuf.toString();
		log.info("sql="+command);
		List<Map<String,Object>> dataList = null;
		try {
			QueryResult dataResult = influxDBConfig.query(command);			
			dataList = influxDBConfig.queryResultProcess(dataResult);
		} catch (Exception e) {
			log.error("DataQueryServiceImpl exception="+e.getLocalizedMessage());
		}		
		return dataList;
	}
	
	@Override
	public List<Map<String,Object>> qrySensorData(String sensorName1,String sensorName2,String  start,String end) {
		String startTime = TimeUtil.getUTC(start);
		String endTime = TimeUtil.getUTC(end);
		StringBuffer commandBuf = new StringBuffer();
		commandBuf.append("select \"time\",\"");
		commandBuf.append(sensorName1);
		commandBuf.append("\",\"");
		commandBuf.append(sensorName2);
		commandBuf.append("\" from smartOil where time >=");
				
		commandBuf.append("'"+startTime+"' and time < '");
		commandBuf.append(endTime+"'");
		String command = commandBuf.toString();
		log.info("sql="+command);
		List<Map<String,Object>> dataList = null;
		try {
			QueryResult dataResult = influxDBConfig.query(command);			
			dataList = influxDBConfig.queryResultProcess(dataResult);
		} catch (Exception e) {
			log.error("DataQueryServiceImpl exception="+e.getLocalizedMessage());
		}		
		return dataList;
	}
	
	@Override
	public List<Map<String,Object>> qrySensorData2(String sensorName1,String  start,String end) {
		String startTime = TimeUtil.getUTC(start);
		String endTime = TimeUtil.getUTC(end);
		StringBuffer commandBuf = new StringBuffer();
		commandBuf.append("select \"time\",\"");	
		commandBuf.append(sensorName1);
		commandBuf.append("\" from smartOil where time >=");
				
		commandBuf.append("'"+startTime+"' and time < '");
		commandBuf.append(endTime+"'");
		String command = commandBuf.toString();
		log.info("sql="+command);
		List<Map<String,Object>> dataList = null;
		try {
			QueryResult dataResult = influxDBConfig.query(command);			
			dataList = influxDBConfig.queryResultProcess(dataResult);
		} catch (Exception e) {
			log.error("DataQueryServiceImpl exception="+e.getLocalizedMessage());
		}		
		return dataList;
	}
}
