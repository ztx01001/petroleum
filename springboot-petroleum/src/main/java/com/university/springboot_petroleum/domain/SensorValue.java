package com.university.springboot_petroleum.domain;

import java.math.BigInteger;

public class SensorValue {
	private int id;
	private String name;
	private float value;
	private BigInteger widgetID;
	
	public SensorValue(int id,String name,float value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}
	
	public SensorValue(int id,String name,float value,BigInteger widgetID) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.widgetID = widgetID;
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

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public BigInteger getWidgetID() {
		return widgetID;
	}

	public void setWidgetID(BigInteger widgetID) {
		this.widgetID = widgetID;
	}
	

}
