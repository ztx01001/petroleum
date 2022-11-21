package com.university.springboot_petroleum.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.university.springboot_petroleum.tcp.TCPRequestClient;
import com.university.springboot_petroleum.util.ByteUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CollectDataTask {

	@Value("${spring.tcp.host}")
	private String host;
	@Value("${spring.tcp.port}")
	private int port;
	@Value("${spring.tcp.request}")
	private String request;
	
	public void run() {
		log.info("sensorCollectTask host="+host+" port="+port);
		TCPRequestClient  tcpReq = new TCPRequestClient(host,port);
		
		byte[] req = ByteUtil.hexStr2Bytes(request);
		ByteUtil.printHex(req, req.length);
		//tcpReq.sendReqNoWait(req);
		tcpReq.sendReqWait(req);
		log.info("sensorCollectTask collect end");
	}
}
