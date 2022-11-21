package com.university.springboot_petroleum.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import com.university.springboot_petroleum.domain.ResponseMessage;

/**
 * 传感器返回报文队列，数据解析线程从中获取数据进行解析
 * 
 */
public class ResponseMessageQueue {
	//使用进程内消息队列 ConcurrentLinkedQueue 线程安全，非阻塞队列
	private static ConcurrentLinkedQueue<ResponseMessage> responseQueue = new ConcurrentLinkedQueue<>();
	//本来不需要增加信号量操作，提高性能，但是worker线程死循环占用CPU太高,实际上封装成阻塞的队列
	private static Semaphore notEmpty = new Semaphore(0);
	//封装队列函数，入队 出队
	//入队
	public static boolean putResponse(ResponseMessage response) {
		//参数检查
		if(null != response) {
			responseQueue.offer(response);
			notEmpty.release();
			return true;
		}else {
			return false;
		}
	}
	//出队
	public static ResponseMessage getResponse()throws InterruptedException{
		//worker 线程获取信号量通知，有数据，处理数据，没有信号量时，在此等待
		notEmpty.acquire();
		return responseQueue.poll();
	} 

}
