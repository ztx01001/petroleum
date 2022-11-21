package com.university.springboot_petroleum.eventproducer;

public interface DisruptorMqService {

	 /**
     * 消息
     * @param message
     */
	public void sendMessage(String message);
}
