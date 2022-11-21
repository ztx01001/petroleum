package com.university.springboot_petroleum.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.util.Constants;

//校验message 是否为空
public class CheckNullHandler extends Handler{
	private static Logger log = LoggerFactory.getLogger(CheckNullHandler.class);

	@Override
	public boolean doHandler(ResponseMessage message) {
		log.info("CheckNullHandler enter");
		boolean result = false;
		if(checkNull(message)) {
			if(next != null) {
				result = next.doHandler(message);
				return result;
			}else {
				return true;
			}
		}else {
			log.error("CheckNullHandler message is empty nor null");
			return false;
			
		}
	
	}
	private boolean checkNull(ResponseMessage message) {
		if(message == null || message.getData() == null || message.getData().length == Constants.Zero)
		{
			log.info("check null is false");
			return false;
		}
			
		return true;
	}
}
