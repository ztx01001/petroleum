package com.university.springboot_petroleum.mqmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.university.springboot_petroleum.message.MessageModel;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.university.springboot_petroleum.eventconsumer.AlertEventHandler;
import com.university.springboot_petroleum.eventfactory.AlertEventFactory;

@Configuration
public class MQManager {

	@Bean("messageModel")
	public RingBuffer<MessageModel> messageModelRingBuffer() {
		//定义用于事件处理的线程池， Disruptor通过java.util.concurrent.ExecutorSerivce提供的线程来触发consumer的事件处理
        ExecutorService executor = Executors.newFixedThreadPool(2);
 
        //指定事件工厂
        AlertEventFactory factory = new AlertEventFactory();
        //指定ringbuffer字节大小，必须为2的N次方（能将求模运算转为位运算提高效率），否则将影响效率
        int bufferSize = 1024 * 256;
 
        //单线程模式，获取额外的性能
        Disruptor<MessageModel> disruptor = new Disruptor<>(factory, bufferSize, executor,
                ProducerType.SINGLE, new BlockingWaitStrategy());
 
        //设置事件业务处理器---消费者
        disruptor.handleEventsWith(new AlertEventHandler());
        
     // 启动disruptor线程
        disruptor.start();
 
        //获取ringbuffer环，用于接取生产者生产的事件
        RingBuffer<MessageModel> ringBuffer = disruptor.getRingBuffer();
 
        return ringBuffer;


	}
	
}
