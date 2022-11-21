package com.university.springboot_petroleum.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.university.springboot_petroleum.cache.GlobalCache;
import com.university.springboot_petroleum.domain.AlertActionMessage;

import com.university.springboot_petroleum.domain.DataStoreInfo;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.queue.AlarmActionQueue;
import com.university.springboot_petroleum.queue.DataAlarmQueue;
import com.university.springboot_petroleum.service.AlertLogService;
import com.university.springboot_petroleum.websocket.WebSocketServer;

public class AlarmTask implements Runnable {

	private static Logger log = LoggerFactory.getLogger(AlarmTask.class);
	private AlertLogService alertLogServiceImpl = null;
	public AlarmTask() {
		this.alertLogServiceImpl = ApplicationContextProvider.getBean(AlertLogService.class);
	}

	@Override
	public void run() {
		doConsumer();
	}

	private void doConsumer() {
		while (true) {
			try {
				log.info("AlarmTask enter");
				DataStoreInfo data = DataAlarmQueue.getData();
				if (data != null && (data.getDataMap().isEmpty() == false)) {
					log.info("AlarmTask get data enter");
					if (GlobalCache.AlarmMainInfoList != null && (GlobalCache.AlarmMainInfoList.isEmpty() == false)) {
						int size = GlobalCache.AlarmMainInfoList.size();
						log.info("AlarmTask size="+size);
						for (int i = 0; i < size; i++) {
							
							AlarmMainInfoDTO alarmMainInfo = GlobalCache.AlarmMainInfoList.get(i);
							String expression = alarmMainInfo.getCondition();
							log.info("AlarmTask alertinfo ="+i+" condition="+expression);
							// 编译表达式
							Expression compiledExp = AviatorEvaluator.compile(expression);
							 // 执行表达式
					        Boolean result = (Boolean) compiledExp.execute(data.getDataMap());
					        //true 产生告警日志
					        if(result) {
					        	log.info("AlarmTask make alarm enter");
					        	int alarmID = alarmMainInfo.getAlarmID();
					        	String alarmName = alarmMainInfo.getAlarmName();					        	
					        	String alarmLevel = alarmMainInfo.getAlarmLevel();
					        	
					        	 Date date = new Date();
					        	 long times = date.getTime();//时间戳
					        	 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					        	 String dateString = formatter.format(date);
					        	 //send websocket message 
					        	 log.info("AlarmTask make alarm send websocket message enter");
					        	 webSocketMessage(alarmID,alarmName,alarmLevel,dateString);
					        	 //write data into alertlog table					        	 
					        	 log.info("AlarmTask make alarm insert data into table enter");
					        	 alertLogServiceImpl.addAlarmLog(alarmID,alarmName,times,alarmLevel);
					        	 //generate alert action task put into queue
					        	 log.info("AlarmTask make alarm put action into queue enter");
					        	 List<AlarmActionDTO> aaList = GlobalCache.AlarmActionMap.get(alarmID);
					        	 if(aaList != null && (aaList.isEmpty() == false)) {
					        		 int actionSize = aaList.size();
					        		 for(int index = 0;index<actionSize;index++) {
					        			 AlarmActionDTO aaction = aaList.get(index);
					        			 AlertActionMessage actionMessage = new AlertActionMessage(aaction.getActionType(),aaction.getActionObject(), aaction.getOperation(), aaction.getContent());
							        	 AlarmActionQueue.putData(actionMessage);
					        		 }
					        	 }
					        	 
					        	 log.info("AlarmTask make alarm end");
					        	 
					        }

						}
					}
				}
			} catch (Exception e) {
				log.error("AlarmTask exception="+e.getLocalizedMessage());
			}

		}
	}
	
	public void webSocketMessage(int alarmID,String alarmName,String alarmLevel,String dateString) {
		
		try {
			List<AlarmConditionDTO> acList = GlobalCache.AlarmConditionMap.get(alarmID);
			List<String> devList = new ArrayList<>();
			if(acList != null && (acList.isEmpty() == false)) {
				int size = acList.size();
				
				for(int i = 0;i<size;i++) {
					devList.add(acList.get(i).getSensorName());
				}
			}
			Map<String, Object> alertMap = new HashMap<>();
			
	    	 alertMap.put("devices", devList);
	    	 alertMap.put("alarmName", alarmName);
	    	 alertMap.put("alarmLevel", alarmLevel);
	    	 alertMap.put("time", dateString);
	    	 alertMap.put("messageType", "alarm");
	    	 
	    	 ObjectMapper mapper = new ObjectMapper(); //转换器 
			 String dataResult = mapper.writeValueAsString(alertMap);
			 log.info("alert task webSocketMessage dataResult="+dataResult);
			 WebSocketServer.sendAll(dataResult);
			
		} catch (Exception e) {
			// TODO: handle exception
			log.error("webSocketMessage exception=");
		}		 
		
	}
}
