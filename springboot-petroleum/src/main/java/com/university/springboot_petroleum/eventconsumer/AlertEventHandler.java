package com.university.springboot_petroleum.eventconsumer;

import java.util.List;
import java.util.Map;

import com.lmax.disruptor.EventHandler;
import com.university.springboot_petroleum.cache.GlobalCache;
import com.university.springboot_petroleum.dto.AlarmActionDTO;
import com.university.springboot_petroleum.dto.AlarmConditionDTO;
import com.university.springboot_petroleum.dto.AlarmMainInfoDTO;
import com.university.springboot_petroleum.message.MessageModel;

import com.university.springboot_petroleum.service.impl.AlertServiceImpl;
import com.university.springboot_petroleum.task.ApplicationContextProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlertEventHandler implements EventHandler<MessageModel> {

	
	private AlertServiceImpl alertServiceImpl;

	public AlertEventHandler() {
		
	}
	
	@Override
	public void onEvent(MessageModel event, long sequence, boolean endOfBatch) {
		try {
			log.info("消费者处理消息开始");

			if (event != null) {
				log.info("消费者消费的信息是：{}", event);
				String message = event.getMessage();
				String[] messageArray = message.split("-");
				if (messageArray != null && (messageArray.length == 2)) {
					String head = messageArray[0];					
					if (head.startsWith("alarm")) {
						log.info("消费者处理消息 alertServiceImpl enter");
						this.alertServiceImpl = ApplicationContextProvider.getBean(AlertServiceImpl.class);
						synchronized (this) {
							List<AlarmMainInfoDTO> alarmMainInfos = alertServiceImpl.qryRunningAlarmMainInfo();
							Map<Integer, List<AlarmConditionDTO>> acMap = alertServiceImpl.qryAlarmCondition();
							Map<Integer, List<AlarmActionDTO>> aaMap = alertServiceImpl.qryAlarmAction();
							log.info("消费者处理消息 alertServiceImpl end");
							if(alarmMainInfos != null && (alarmMainInfos.isEmpty() == false)) {
								log.info("消费者处理消息 update alertinfo list enter");
								GlobalCache.AlarmMainInfoList.clear();
								GlobalCache.AlarmMainInfoList.addAll(alarmMainInfos);
								GlobalCache.AlarmConditionMap.clear();
								GlobalCache.AlarmConditionMap.putAll(acMap);
								GlobalCache.AlarmActionMap.clear();
								GlobalCache.AlarmActionMap.putAll(aaMap);
								log.info("消费者处理消息 update alertinfo list end="+GlobalCache.AlarmMainInfoList.size());
								
							}
						}
						
					} 
				}
			}

		} catch (Exception e) {
			log.info("消费者处理消息失败"+e.getLocalizedMessage());

		}
		log.info("消费者处理消息结束");
	}

}
