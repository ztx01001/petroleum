package com.university.springboot_petroleum.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;
import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.queue.ResponseMessageQueue;
import com.university.springboot_petroleum.serial.SerialPortManager;
import com.university.springboot_petroleum.util.ByteUtil;
import com.university.springboot_petroleum.util.Constants;
import com.university.springboot_petroleum.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SeriesDataTask {

	@Value("${spring.tcp.request}")
	private String request;
	@Value("${spring.tcp.host}")
	private String host;
	
	public void run() {
		log.info("getDataBySerial enter");
		byte[] req = ByteUtil.hexStr2Bytes(request);
		byte[] readBuffer = new byte[Constants.PduLen];		
		int dataLen = 0;
		byte[] dataBuffer = new byte[Constants.PduLen]; 
		SerialPort serialPort = null;
		try {
			serialPort =SerialPortManager.openPort("COM4", 9600);
			
			if(serialPort.isOpen()) {
				log.info("serial is opened");
				serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
				SerialPortManager.sendToPort(serialPort, req);
//				while(serialPort.bytesAvailable()>0) {
//					log.info("read serail enter");
//					byte[] newData = new byte[serialPort.bytesAvailable()];//创建一个字节数组，长度为可读取的字节长度
//			        int numRead = serialPort.readBytes(newData, newData.length);//将串口中可读取的数据读入字节数组，返回值为本次读取到的字节长度
//			        System.arraycopy(newData, 0, readBuffer, dataLen, numRead);
//			        dataLen = dataLen + numRead;
//			        log.info("read byte len="+numRead);
//				}
				 while (true)
				   {				     
				      int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
				      log.info("Read " + numRead + " bytes."+dataLen+" readBuffer="+StringUtil.bytes2Hex(readBuffer));
				      System.arraycopy(readBuffer,0,dataBuffer,dataLen,numRead);
				      dataLen += numRead;
				      
				      if(dataLen >= Constants.PduLen)
				    	  break;

				   }
				 log.info("data ="+StringUtil.bytes2Hex(dataBuffer));
				 ResponseMessage response = new ResponseMessage(dataBuffer,dataLen,host);
					ResponseMessageQueue.putResponse(response);
				
			}
			
		} catch (Exception e) {
			
			log.error("getDataBySerial exception="+e.getLocalizedMessage());
		}finally {
			SerialPortManager.closePort(serialPort);
		}
	}
}
