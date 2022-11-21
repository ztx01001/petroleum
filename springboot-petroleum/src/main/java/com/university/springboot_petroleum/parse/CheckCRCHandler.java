package com.university.springboot_petroleum.parse;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.util.ByteUtil;
import com.university.springboot_petroleum.util.CRCUtil;

public class CheckCRCHandler extends Handler{
	private static Logger log = LoggerFactory.getLogger(CheckCRCHandler.class);

	@Override
	public boolean doHandler(ResponseMessage message) {
		log.info("CheckCRCHandler enter");
		boolean result = false;
		if(crc(message)) {
			if(null != next) {
				result = next.doHandler(message);
				return result;
			}else {
				return true;
			}
			
		}else {
			
			log.error("CheckCRCHandler crc not right");
			return false;
		}
		
	}
	
	private boolean crc(ResponseMessage message) {
		boolean result = false;
		byte[] msgData = message.getData();
		int len = message.getLen();
		//需要进行CRC校验的数据
		byte[] crcData = Arrays.copyOfRange(msgData, 0, (len - 2));
		log.info("crc data:");
		ByteUtil.printHex(crcData, len-2);
		//消息中的crc校验数据
		byte[] crcOrig = Arrays.copyOfRange(msgData, (len - 2), len);
		log.info("crc result origal=");
		ByteUtil.printHex(crcOrig, 2);
		//校验后的结果
		byte[] crcResult = CRCUtil.getCRCModbus(crcData);
		log.info("crc result checked=");
		ByteUtil.printHex(crcResult, 2);
		if(Arrays.equals(crcOrig, crcResult)) {
			log.info("crc is right----");
			return true;
		}
		
		return result;
	}
}
