package com.university.springboot_petroleum.service;

import java.util.List;
import java.util.Map;

public interface DataQueryService {

	public List<Map<String,Object>> qryData(String start,String end);
	
	public List<Map<String,Object>> qrySensorData(String sensorName1,String sensorName2,String  start,String end);
	
	public List<Map<String,Object>> qrySensorData2(String sensorName1,String  start,String end);
}
