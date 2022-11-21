package com.university.springboot_petroleum.parse;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.util.Constants;

public class HeadHandler extends Handler{

	private static Logger log = LoggerFactory.getLogger(HeadHandler.class);
	@Override
	public boolean doHandler(ResponseMessage message) {
		log.info("HeadHandler enter");
		boolean result = false;
		if(checkHead(message)) {
			if(null != next) {
				result = next.doHandler(message);
				return result;
			}else {
				return true;
			}
		}else {
			log.error("HeadHandler message head is not right");
			return false;
		}
		
	}
	
	private boolean checkHead(ResponseMessage message) {
		boolean result = false;
		byte[] data = message.getData();
		byte[] head = Arrays.copyOfRange(data, 0, Constants.HeadLen);
		if(null != head) {
			result = Arrays.equals(Constants.MessageHead, head);
			log.info("checkHead is result="+result);
		}
		return result;
	}
}
