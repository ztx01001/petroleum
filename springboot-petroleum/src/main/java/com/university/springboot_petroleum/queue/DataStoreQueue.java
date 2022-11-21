package com.university.springboot_petroleum.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.DataStoreInfo;

public class DataStoreQueue {
	private static final Logger log = LoggerFactory.getLogger(DataStoreQueue.class);

	// 保存数据队列 LinkedBlockingQueue 线程安全且阻塞的队列，队列空时，take会阻塞
	private static LinkedBlockingQueue<DataStoreInfo> storeQueue = new LinkedBlockingQueue<>();

	public static void putData(DataStoreInfo data) {
		try {
			if (null != data) {
				storeQueue.put(data);
			}
		} catch (Exception e) {
			log.error("DataStoreQueue put data exception=" + e.getLocalizedMessage());
		}

	}

	public static DataStoreInfo getData() throws InterruptedException {
		return storeQueue.take();
	}

}
