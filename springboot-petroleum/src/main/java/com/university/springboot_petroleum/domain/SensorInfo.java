package com.university.springboot_petroleum.domain;

import java.math.BigInteger;

public class SensorInfo {
	private int id;
	private String name;
	private String dispName;
	private String attributeName;
	private int addr;
	private BigInteger widgetId;
	
	public SensorInfo() {
		
	}
	public SensorInfo(int id,String name,String dispName,String attributeName,int addr) {
		this.id = id;
		this.name = name;
		this.dispName = dispName;
		this.attributeName = attributeName;
		this.addr = addr;
	}
	
	public SensorInfo(int id,String name,String dispName,String attributeName,int addr,BigInteger widgetID) {
		this.id = id;
		this.name = name;
		this.dispName = dispName;
		this.attributeName = attributeName;
		this.addr = addr;
		this.widgetId = widgetID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDispName() {
		return dispName;
	}
	public void setDispName(String dispName) {
		this.dispName = dispName;
	}
	public int getAddr() {
		return addr;
	}
	public void setAddr(int addr) {
		this.addr = addr;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public BigInteger getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(BigInteger widgetId) {
		this.widgetId = widgetId;
	}

	
}
