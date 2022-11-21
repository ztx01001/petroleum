package com.university.springboot_petroleum.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.university.springboot_petroleum.domain.DevCtlCmdMessage;
/**
 *设备控制命令队列，设备控制线程从中获取命令，下发 
 *客户的设备目前还没有实现远程控制，先开发，以备后用
 */
public class DevCtlCmdQueue {
	//锁
	public static Lock lock = new ReentrantLock();
	//条件 跟锁一起使用
	public static Condition notEmpty = lock.newCondition();
	//下发控制设备命令的队列
	public static ConcurrentLinkedQueue<DevCtlCmdMessage> queue = new ConcurrentLinkedQueue<>();

}
