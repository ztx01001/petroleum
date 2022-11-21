package com.university.springboot_petroleum.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.springboot_petroleum.cache.GlobalCache;
import com.university.springboot_petroleum.domain.DataStoreInfo;
import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.domain.SensorInfo;
import com.university.springboot_petroleum.domain.SensorValue;
import com.university.springboot_petroleum.parse.CheckCRCHandler;
import com.university.springboot_petroleum.parse.CheckNullHandler;
import com.university.springboot_petroleum.parse.DataLenHandler;
import com.university.springboot_petroleum.parse.Handler;
import com.university.springboot_petroleum.parse.HeadHandler;
import com.university.springboot_petroleum.queue.DataAlarmQueue;
import com.university.springboot_petroleum.queue.DataStoreQueue;
import com.university.springboot_petroleum.queue.ResponseMessageQueue;
import com.university.springboot_petroleum.util.Constants;
import com.university.springboot_petroleum.websocket.WebSocketServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

//传感器返回报文解析线程
public class SensorParseTask implements Runnable{
	private static Logger log = LoggerFactory.getLogger(SensorParseTask.class);
	
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public SensorParseTask() {
		this.kafkaTemplate = ApplicationContextProvider.getBean(KafkaTemplate.class);
	}

	//runnable 接口
	@Override
	public void run() {
		doConsumer();
	}
	//解析工作开始,死循环,一直做,根据信号量通知来进行解析
	private void doConsumer() {
		Handler checkNull = new CheckNullHandler();
		Handler dataLen = new DataLenHandler();
		Handler checkHead = new HeadHandler();
		Handler checkCRC = new CheckCRCHandler();
		Handler.Builder builder = new Handler.Builder();
		builder.add(checkNull).add(dataLen).add(checkHead).add(checkCRC);
		//builder.add(checkNull).add(dataLen).add(checkHead);
		Handler handler = builder.build();
		while(true) {
			try {
				//如果没有报文，将阻塞在此
				log.info("SensorParseTask waiting...");
				ResponseMessage message = ResponseMessageQueue.getResponse();
				log.info("SensorParseTask get response message start check data");
				//校验报文是否错误
				
				boolean checkB = handler.doHandler(message);
		        log.info("check result="+checkB);
				//返回报文头、报文长度、crc校验没有问题时，进行数据解析
				if(checkB) {
					log.info("message is ok,start parse data");
					byte[] pdu = message.getData();
					byte[] parseData = new byte[Constants.MessageLen];
					System.arraycopy(pdu, Constants.PduSPostion, parseData, 0, Constants.MessageLen);
					ByteBuf buf = Unpooled.wrappedBuffer(parseData);
					
					List<SensorValue>  webSocketList = new ArrayList<SensorValue>();
					Map<String,Object> dataStorefields = new HashMap<String,Object>();
					Map<String,Object> alertfields = new HashMap<String,Object>();
					/*
					for(int i = 0; i < Constants.SensorCount; i++ ) {
						float value = buf.readFloat();
						SensorInfo sensorInfo = GlobalCache.SensorInfoMap.get(Integer.valueOf(i));
						if(null != sensorInfo) {
							fields.put(sensorInfo.getName(), Float.valueOf(value));
						}
					}*/
					//将byte[]数据解析成map key：传感器名字 value：数值
					parse(buf, webSocketList,dataStorefields,alertfields);
					//解析成功
					if(null != webSocketList && webSocketList.isEmpty() == false) {
						//将结果通过websocket 发送前端
						ObjectMapper mapper = new ObjectMapper(); //转换器 
						Map<String, Object> dataMap = new HashMap<>();
						dataMap.put("messageType", "data");
						dataMap.put("data", webSocketList);
						Date now = new Date();
						String timeStr = format.format(now.getTime());
						dataMap.put("time", timeStr);
						String dataResult = mapper.writeValueAsString(dataMap);
						log.info("SensorParseTask dataResult="+dataResult);
						WebSocketServer.sendAll(dataResult);
						//将结果放到数据保存队列中
						DataStoreInfo dataStore  = new DataStoreInfo(dataStorefields, "smartOil");
						DataStoreQueue.putData(dataStore);
						//将结果放到告警处理队列中
						DataStoreInfo alertData = new DataStoreInfo(alertfields, "smartOil");
						DataAlarmQueue.putData(alertData);
						String kafkamessage = mapper.writeValueAsString(dataStorefields);
						log.info("kafka message="+kafkamessage);
						kafkaTemplate.send("topic001", kafkamessage);
						
					}
					//释放资源
					webSocketList = null;
					pdu = null;
					parseData = null;
					buf = null;
				}
				
				
				message = null;
			}catch(Exception e) {
				log.error("SensorParseTask doConsumer exception"+e.getLocalizedMessage());
			}finally {
				
			}
		}
	}
	
	//解析数据
	private void parse(ByteBuf buf,List<SensorValue>  webSocketList,Map<String,Object> dataStorefields,Map<String,Object> alertfields) {
		try {
			//DecimalFormat df = new DecimalFormat("0.00");
			for(int i = 0; i < Constants.SensorCount; i++ ) {
				float value = buf.readFloat();
				//Float valueFloat =  Float.parseFloat(df.format(value));
				float saveValue = (float)(Math.round(value*100))/100;
				BigDecimal bd = new BigDecimal(value);//创建一个bd对象，将要转换的值value传入构造函数
				bd = bd.setScale(2, RoundingMode.HALF_UP);//调用setScale方法进行数据格式化，保留两位小数，采用四舍五入规则

				log.info("parse float valueFloat="+saveValue+"  float="+value);
				SensorInfo sensorInfo = GlobalCache.SensorInfoMap.get(Integer.valueOf(i+1));
				
				if(null != sensorInfo) {
					SensorValue sensorValue = new SensorValue(i+1, sensorInfo.getDispName(), saveValue,sensorInfo.getWidgetId());
					webSocketList.add(sensorValue);
					//websocketfields.put(sensorInfo.getName(),saveValue);
					dataStorefields.put(sensorInfo.getName(), bd);
					alertfields.put(sensorInfo.getName(), saveValue);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("SensorParseTask parse exception ="+e.getLocalizedMessage());
			webSocketList = null;
			dataStorefields = null;
			alertfields = null;
		}
	}
}
