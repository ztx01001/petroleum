package com.university.springboot_petroleum.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.springboot_petroleum.exception.DataEmptyException;
import com.university.springboot_petroleum.service.AlertLogService;
import com.university.springboot_petroleum.util.PageList;
import com.university.springboot_petroleum.vo.AlarmLogVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AlertLogController {
	@Autowired
	AlertLogService alertLogServiceImpl;
	//
	@GetMapping(value = "/api/alarmlog/")
	public PageList<AlarmLogVo> qryAlarmLog(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize,@RequestParam(value="statusID",required = false, defaultValue = "1") int statusID,@RequestParam(value="alarmID",required = false, defaultValue = "0") int alarmID){
		log.info("get /api/alertlog/ enter");
		if(pageNum == 0) {
			pageNum = 1;
		}
		if(pageSize == 0) {
			pageSize = 20;
		}
		if(statusID == 0) {			
			statusID = 1;
		}
		
		//默认待处理状态		
		 PageList<AlarmLogVo> pageList = new PageList<AlarmLogVo>();
		 int totalCount = alertLogServiceImpl.countAll(statusID,alarmID);
		 List<AlarmLogVo> list = alertLogServiceImpl.qryAlarmLogByPage(pageNum, pageSize,statusID,alarmID);
		 pageList.setTotalRows(totalCount);
		 pageList.setPage(pageNum);
		 int pages = 0;//总页数
		 if(totalCount%pageSize == 0) {
			 pages = totalCount/pageSize;
		 }else {
			 pages = totalCount/pageSize + 1;
		 }
		 pageList.setPages(pages);
			pageList.setList(list);			
		
		if(list == null || list.isEmpty()) {
			throw new DataEmptyException("no alarm log detail info");
		}else {
			return pageList;
		}
		
	} 
	
	@GetMapping(value = "/api/alarmlog")
	public List<AlarmLogVo> qryAlarmLogByID(@RequestParam("alarmID") int alarmID){
		List<AlarmLogVo> result = alertLogServiceImpl.qryAlarmLogByID(alarmID);
		if(result == null || result.isEmpty()) {
			throw new DataEmptyException("no alarm log detail info");
		}else {
			return result;
		}
	}
	
//	@PutMapping(value = "/api/alarmlog")
//	public boolean updateAlarmLog(@RequestParam("alarmLogID") int alertLogID,@RequestParam("statusID") int statusID) {
//		log.info("put /api/alertlog enter");
//		boolean result = false;
//		result = alertLogServiceImpl.updateAlarmLogStatus(alertLogID, statusID);
//		return result;
//	}
	@PutMapping(value = "/api/alarmlog")
	public boolean batchUpdateAlarmLog(@RequestBody Map params) {
		int statusID = 0;
		List<Integer> alarmLogIDList = null;
		boolean result = false;
		try {
			statusID = ((Integer)params.get("statusID")).intValue();
			alarmLogIDList = (List<Integer>)params.get("alarmLogIDList");
		} catch (Exception e) {
			log.error("batchUpdateAlarmLog params exception="+e.getLocalizedMessage());
			return false;
		}
		if((alarmLogIDList != null) &&(alarmLogIDList.isEmpty() == false) ) {
			result = alertLogServiceImpl.batchUpdateAlarmLogStatus(statusID, alarmLogIDList);
		}
		
		 
		return result;
	}
	
//	@DeleteMapping(value = "/api/alarmlog")
//	public boolean delAlarmLog(@RequestParam("alarmLogID") int alertLogID) {
//		log.info("delete /api/alertlog enter");
//		boolean result = false;
//		result = alertLogServiceImpl.delAlarmLog(alertLogID);
//		return result;
//	}
	@DeleteMapping(value = "/api/alarmlog")
	public boolean batchDelAlarmLog(@RequestBody Map params) {
		boolean result = false;
		List<Integer> alarmLogIDList = null;
		try {
			alarmLogIDList = (List<Integer>)params.get("alarmLogIDList");
		} catch (Exception e) {
			log.error("batchDelAlarmLog params exception");
			return false;
		}
		if((alarmLogIDList != null) && (alarmLogIDList.isEmpty() == false)) {
			result = alertLogServiceImpl.batchDelAlarmLog(alarmLogIDList);
		}
		return result;
	}

}
