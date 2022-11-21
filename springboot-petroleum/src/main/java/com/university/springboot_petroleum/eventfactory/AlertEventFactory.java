package com.university.springboot_petroleum.eventfactory;

import com.lmax.disruptor.EventFactory;
import com.university.springboot_petroleum.message.MessageModel;

public class AlertEventFactory implements EventFactory<MessageModel>{

	@Override
	public MessageModel newInstance() {
		return new MessageModel();
	}
}
