package com.university.springboot_petroleum.domain;

import java.util.Map;

public class DataStoreInfo {

	private String host;
	private Map<String,Object> dataMap;
	private String measureMents;
	public DataStoreInfo(Map<String, Object> dataMap,String measureMents) {
		this.dataMap = dataMap;
		this.measureMents = measureMents;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	public String getMeasureMents() {
		return measureMents;
	}
	public void setMeasureMents(String measureMents) {
		this.measureMents = measureMents;
	}
	
	
}
