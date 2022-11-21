package com.university.springboot_petroleum.serial;

import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;


import com.pi4j.io.serial.StopBits;
import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.queue.ResponseMessageQueue;
import com.university.springboot_petroleum.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialUtil {
	

	private static final Serial serial = SerialFactory.createInstance();
	// private static SerialConfig config = new SerialConfig();
		 static {
			//设置serial回调接口处理程序
			serial.addListener(new SerialDataEventListener() {

				@Override
				public void dataReceived(SerialDataEvent event) {

					try {
						int len = event.length();
						byte[] datas = event.getBytes();
						//判断串口接收到 的上报报文是否满足长度要求
						if(datas != null){
							ResponseMessage response = new ResponseMessage(datas,len,"serial");
							ResponseMessageQueue.putResponse(response);
						}else{
							//System.out.println("receive data don't meet demand="+event.getHexByteString());
							log.info("receive data don't meet demand="+event.getHexByteString());
						}
						

					} catch (Exception e) {
						//e.printStackTrace();
						log.error("serial dataReceived exception="+e.getLocalizedMessage());
					}
				}
			});
			
			try{
				// create serial config object
				// SerialPort.getDefaultPort();
	            SerialConfig config = new SerialConfig();
	            //config.device("/dev/ttyUSB0")
	            config.device("COM10")
	            //config.device("/dev/ttyUSB0")
	            .baud(Baud._115200)
	            .dataBits(DataBits._8)
	            .parity(Parity.NONE)
	            .stopBits(StopBits._1)
	            .flowControl(FlowControl.NONE);
	            //System.out.println("start open serial ---");
	            log.info("start open serial ---");
	            if(serial.isClosed()){
	            	serial.open(config);
	            	//System.out.println("open serial end---");
	            	log.info("open serial end---");
	            }
	            
			}catch(Exception e){
				//System.out.println("serial init exception="+e.getMessage());
				log.error("serial init exception="+e.getMessage());
			}
		}
		public SerialUtil(){}
		
		public static void send(String data){
			try{
				//System.out.println("start send data----");
				log.info("start send data----");
				serial.write(data);
				serial.flush();
				//System.out.println("start send data end-----");
				log.info("start send data end-----");
			}catch(Exception e){
				//System.out.println("send data exception="+e.getMessage());
				log.error("send data exception="+e.getMessage());
			}
		}
		
		public static void send(byte[] req){
			try{
				if(null == req){
					//System.out.println("SerialUtil send req is null XXX");
					log.error("SerialUtil send req is null XXX");
					return;
				}
				//System.out.println("start send byte[] data ="+StringUtil.bytes2Hex(req));
				log.info("start send byte[] data ="+StringUtil.bytes2Hex(req));
				serial.write(req,0,req.length);
				//serial.write(req);
				serial.flush();
				log.info("start send byte[] data end-----");
				//System.out.println("start send byte[] data end-----");
			}catch(Exception e){
				//System.out.println("send data exception="+e.getMessage());
				log.error("send data exception="+e.getMessage());
				reOpen();
				log.error("send data exception then reopen end");
				//System.out.println("send data exception then reopen end");
			}
		}
		
		public static void reOpen(){
			try{
				// create serial config object
	            SerialConfig config = new SerialConfig();
	            //config.device("/dev/ttyUSB0")
	            //config.device("COM10")
	            config.device("/dev/ttyUSB0")
	            .baud(Baud._115200)
	            .dataBits(DataBits._8)
	            .parity(Parity.NONE)
	            .stopBits(StopBits._1)
	            .flowControl(FlowControl.NONE);
	            //System.out.println("start open serial ---");
	            log.info("start reopen serial ---");
	            if(serial.isClosed()){
	            	serial.open(config);
	            	//System.out.println("open serial end---");
	            	log.info("reopen serial end---");
	            }
	            
			}catch(Exception e){
				//System.out.println("serial init exception="+e.getMessage());
				log.error("reopen serial init exception="+e.getMessage());
			}
			
			serial.addListener(new SerialDataEventListener() {

				@Override
				public void dataReceived(SerialDataEvent event) {

					try {
						int len = event.length();
						byte[] datas = event.getBytes();
						//判断串口接收到 的上报报文是否满足长度要求
						if(datas != null){
							ResponseMessage response = new ResponseMessage(datas,len,"serial");
							ResponseMessageQueue.putResponse(response);
							
						}else{
							log.error("receive data don't meet demand="+event.getHexByteString());
						}
						

					} catch (Exception e) {
						log.error("serial reopen exception="+e.getLocalizedMessage());
					}
				}
			});
		}

	
}
