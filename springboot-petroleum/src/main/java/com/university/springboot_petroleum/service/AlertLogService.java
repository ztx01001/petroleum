package com.university.springboot_petroleum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.university.springboot_petroleum.vo.AlarmLogVo;

@Service
public interface AlertLogService {
	
	public boolean addAlarmLog(int alarmID,String alarmName,long time,String severity);
	
	public List<AlarmLogVo> qryAlarmLogByID(int alarmID);

	public List<AlarmLogVo> qryAlarmLog();
	
	public List<AlarmLogVo> qryAlarmLogByPage(int pageNum,int pageSize,int statusID,int alarmID);
	
	public int countAll(int statusID,int alarmID);
	
	public boolean updateAlarmLogStatus(int alertLogId,int status);
	
	public boolean batchUpdateAlarmLogStatus(int statusID,List<Integer> alarmLogIDList);
	
	public boolean delAlarmLog(int alarmLogId);
	
	public boolean batchDelAlarmLog(List<Integer> alarmLogIDList);
}
