package com.university.springboot_petroleum.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fazecast.jSerialComm.SerialPort;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.queue.ResponseMessageQueue;
import com.university.springboot_petroleum.serial.SerialPortManager;
import com.university.springboot_petroleum.serial.SerialUtil;
import com.university.springboot_petroleum.tcp.TCPRequestClient;
import com.university.springboot_petroleum.util.ByteUtil;
import com.university.springboot_petroleum.util.Constants;
import com.university.springboot_petroleum.util.StringUtil;

//定时发送请求，获取报文，整个系统的驱动器
@Configuration  //主要用户标识配置类，兼备Component的效果
@EnableScheduling //开启定时任务
public class SensorCollectionTask {
	private static Logger log = LoggerFactory.getLogger(SensorCollectionTask.class);
	@Value("${spring.tcp.host}")
	private String host;
	@Value("${spring.tcp.port}")
	private int port;
	@Value("${spring.tcp.request}")
	private String request;
	@Value("${spring.serial.com}")
	private String com4;

	//@Autowired
	//TCPRequestClient tcpRequest;
	//定时任务，每十秒钟发送一次请求到串口服务器，目前发送报文固定，需要将报文拆出来，可配置
	//@Scheduled(cron="0/10 * * * * ?")
	public void sensorCollectionTask() {
		
		Socket socket = null;
		OutputStream out = null;
		InputStream in = null;
		//一次接收报文的数组
		byte[] rectime = null;
		//接收报文的总结果
		byte[] result = null;
		int len = 0;
		int destPos = 0;
		
		try {			
			//入参检查
			if(host ==null || host.isEmpty() || port == 0) {
				log.error("host is null or empty, port is zero");
				return;
			}
			//创建串口转以太网口socket连接
			socket = new Socket(host,port);
			//设置socket连接为立即发送
			socket.setTcpNoDelay(true);
			//设置socket连接读超时时间
			socket.setSoTimeout(2000);
			//建立连接后，获取输出流输入流
			out = socket.getOutputStream();
			in = socket.getInputStream();
			//要发送的请求数据
			byte[] req = {0x01,0x03,0x00,0x00,0x00,0x20,0x44,0x12};
			log.info("send request start---");
			out.write(req);
			out.flush();
			//关闭socket输出流,向服务端发送发送结束标识符
			socket.shutdownOutput();
			log.info("send request end------");
			//一次接收的字节数组
			rectime = new byte[1024];
			//最后接收完成的结果数组
			result = new byte[4096];
			//接收的次数统计，如果有分包的话
			int count = 0;
			//如果服务端不主动断链，等待读超时
			while((len = in.read(rectime)) != -1) {
				log.info(count+" time receive data len="+len);
				System.arraycopy(rectime, 0, result, destPos, len);
				destPos += len;
				log.info(count+" time receive data end destposition="+destPos);
				count++;
			}
			
		}catch(SocketTimeoutException e) {
			//读超时异常时，将最后接收的结果保存到消息队列中，等待工作线程处理返回传感器数据报文
			log.error("SocketTimeoutException enter");
			if(0 != destPos) {
				ResponseMessage message = new ResponseMessage(result,destPos,host);
				//将返回报文放入队列中
				ResponseMessageQueue.putResponse(message);
				log.error("response message has put into queue");
				
			}else {
				log.error("error data length is 0");
			}
		}catch(SocketException e) {
			log.error("SocketException enter");
		}catch(IOException e) {
			log.error("IOException enter");
		}catch(Exception e) {
			log.error("Exception enter");
		}finally {
			//整个线程处理完毕后，释放资源
			log.info("sensorCollectionTask finnaly start");
			try {
				if(out != null) {
					out.close();
				}
				if(in != null) {
					in.close();
				}
				if(socket != null) {
					socket.close();
				}
			}catch(Exception e) {
				
			}
		}
	}
	
	//@Scheduled(cron="0/30 * * * * ?")
	public void sensorCollectTask() {
		log.info("sensorCollectTask host="+host+" port="+port);
		TCPRequestClient  tcpReq = new TCPRequestClient(host,port);
		
		byte[] req = ByteUtil.hexStr2Bytes(request);
		//ByteUtil.printHex(req, req.length);
		tcpReq.sendReqNoWait(req);	
		log.info("sensorCollectTask collect end");
	}
	//@Scheduled(cron="0/30 * * * * ?")
	public void sensorCollectBySerial() {
		log.info("sensorCollectBySerial enter");
		byte[] req = ByteUtil.hexStr2Bytes(request);
		try {
			SerialUtil.send(req);
		} catch (Exception e) {
			log.error("send serial data exception="+e.getLocalizedMessage());
		}
	}
	
	//@Scheduled(cron="0/30 * * * * ?")
	public void getDataBySerial() {
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
