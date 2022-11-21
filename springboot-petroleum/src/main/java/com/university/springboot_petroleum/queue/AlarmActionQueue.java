package com.university.springboot_petroleum.queue;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.university.springboot_petroleum.domain.AlertActionMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlarmActionQueue {

	private static Lock lock = new ReentrantLock();
	
	private static Condition notEmpty = lock.newCondition();
	
	private static Queue<AlertActionMessage> actionQueue = new LinkedList<>();
	
	public static void putData(AlertActionMessage action) {
		AlarmActionQueue.lock.lock();
		try {
			AlarmActionQueue.actionQueue.offer(action);
			AlarmActionQueue.notEmpty.signalAll();
		} catch (Exception e) {
			log.error("AlarmActionQueue putData exception="+e.getLocalizedMessage());
		}finally {
			AlarmActionQueue.lock.unlock();
		}
	}
	
	public static AlertActionMessage getData() {
		AlarmActionQueue.lock.lock();
		AlertActionMessage data = null;
		try {
			log.info("AlarmActionQueue getdata isempty start");
			while (AlarmActionQueue.actionQueue.isEmpty()) {
				log.info("AlarmActionQueue getdata await start");
				AlarmActionQueue.notEmpty.await();
				log.info("AlarmActionQueue getdata await end");
			}
			log.info("AlarmActionQueue getdata while end");
			data = AlarmActionQueue.actionQueue.poll();
			
		} catch (Exception e) {
			
			log.error("AlarmActionQueue getData exception="+e.getLocalizedMessage());
		}finally {
			AlarmActionQueue.lock.unlock();
		}
		
		return data;
	}
}
