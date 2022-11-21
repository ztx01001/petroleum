package com.university.springboot_petroleum.vo;

import lombok.Data;

@Data
public class AlarmInfoVo {
	private int alarmID;
	private String alarmName;
	private String alarmLevel;
	private String createTime;
	private int status;
	public AlarmInfoVo(int alarmID,String alarmName,String alarmLevel,String createTime,int status) {
		this.alarmID = alarmID;
		this.alarmName = alarmName;
		this.alarmLevel = alarmLevel;
		this.createTime = createTime;
		this.status = status;
	}

}
