package com.university.springboot_petroleum.vo;



import lombok.Data;

@Data
public class AlarmLogVo {
	private int logID;
	private int alarmID;
	private String alarmName;	
	private String createTime;
	private String alarmLevel;
	private int status;
	private String statusValue;
	
	public AlarmLogVo(int logID,int alarmID, String alarmName,String createTime,String severity,int status) {
		this.logID = logID;
		this.alarmID = alarmID;
		this.alarmName = alarmName;		
		this.createTime = createTime;
		this.alarmLevel = severity;
		this.status = status;
	}
	
	public AlarmLogVo(int logID,int alarmID, String alarmName,String createTime,String severity,String status) {
		this.logID = logID;
		this.alarmID = alarmID;
		this.alarmName = alarmName;		
		this.createTime = createTime;
		this.alarmLevel = severity;
		this.statusValue = status;
	}
	
	public AlarmLogVo(int logID,int alarmID, String alarmName,String createTime,String severity,int status, String statusValue) {
		this.logID = logID;
		this.alarmID = alarmID;
		this.alarmName = alarmName;		
		this.createTime = createTime;
		this.alarmLevel = severity;
		this.status = status;
		this.statusValue = statusValue;
	}

}
