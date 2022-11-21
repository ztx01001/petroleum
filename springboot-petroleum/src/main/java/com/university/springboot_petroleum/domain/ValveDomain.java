package com.university.springboot_petroleum.domain;

//阀门
public class ValveDomain {
	public String name;
	public String state;
	
	public ValveDomain(String name,String state) {
		this.name = name;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}	

}
