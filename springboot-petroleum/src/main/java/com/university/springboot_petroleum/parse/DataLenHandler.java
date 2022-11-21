package com.university.springboot_petroleum.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.util.Constants;

//校验消息长度是否符合预期
public class DataLenHandler extends Handler{

	private static Logger log = LoggerFactory.getLogger(DataLenHandler.class);
	@Override
	public boolean doHandler(ResponseMessage message) {
		log.info("DataLenHandler enter");
		boolean result = false;
		if(checkDataLen(message)) {
			if(next != null) {
				result = next.doHandler(message);
				return result;
			}else {
				return true;
			}
		}else {
			log.error("DataLenHandler length of message is not right");
			return false;
		}
		
	}
	private boolean checkDataLen(ResponseMessage message) {
		boolean result = true;
		log.info("DataLenHandler checkDataLen getLen="+message.getLen()+" dataLen="+message.getData().length);
		//if((message.getLen() != Constants.PduLen) || (message.getData().length < Constants.PduLen)) {
		if((message.getLen() != Constants.PduLen)) {
			result = false;
			log.info("checkDataLen result false");
		}
		return result;
	}
}
