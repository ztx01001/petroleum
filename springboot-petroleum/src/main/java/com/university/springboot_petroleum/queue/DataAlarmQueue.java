package com.university.springboot_petroleum.queue;


import java.util.LinkedList;
import java.util.Queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.DataStoreInfo;

public class DataAlarmQueue {
	private static final Logger log = LoggerFactory.getLogger(DataAlarmQueue.class);
	// 锁
	private static Lock lock = new ReentrantLock();
	// 条件 跟锁一起使用
	private static Condition notEmpty = lock.newCondition();
	//
	private static Queue<DataStoreInfo> alarmQueue = new LinkedList<>();

	//
	public static void putData(DataStoreInfo data) {
		DataAlarmQueue.lock.lock();
		try {
			DataAlarmQueue.alarmQueue.offer(data);
			DataAlarmQueue.notEmpty.signalAll();
		} catch (Exception e) {
			log.error("DataAlarmQueue putData exception="+e.getLocalizedMessage());
		} finally {
			DataAlarmQueue.lock.unlock();
		}
	}

	public static DataStoreInfo getData() {
		DataAlarmQueue.lock.lock();
		DataStoreInfo data = null;
		try {
			log.info("DataAlarmQueue getdata isempty start");
			while (DataAlarmQueue.alarmQueue.isEmpty()) {
				log.info("DataAlarmQueue getdata await start");
				DataAlarmQueue.notEmpty.await();
				log.info("DataAlarmQueue getdata await end");
			}
			log.info("DataAlarmQueue getdata while end");
			data = DataAlarmQueue.alarmQueue.poll();
		} catch (Exception e) {
			log.error("DataAlarmQueue getData exception="+e.getLocalizedMessage());
		} finally {
			DataAlarmQueue.lock.unlock();
		}
		return data;
	}

}
