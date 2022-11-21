package com.university.springboot_petroleum.task;

import java.util.concurrent.ArrayBlockingQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//整个系统的核心任务，有数据解析任务、数据存储任务、告警引擎、告警执行任务
@Component
public class WorkerThreadPool implements ApplicationRunner{
	private static final Logger log = LoggerFactory.getLogger(WorkerThreadPool.class);
	
	/**
	 * 线程池，使用ThreadPoolExecutor 不要使用Executors去创建
	 * 原因：自主可控 （Executors底层也是使用ThreadPoolExecutor创建）
	 */

	private static final int CORE_POOL_SIZE = 4;
	private static final int MAX_POOL_SIZE = 8;
	private static final int QUEUE_CAPACITY = 20;
	private static final Long KEEP_ALIVE_TIME = 1L;
	//创建线程池
	public static final ExecutorService executorService = new ThreadPoolExecutor(
			CORE_POOL_SIZE,
			MAX_POOL_SIZE,
			KEEP_ALIVE_TIME,
			TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(QUEUE_CAPACITY),
			new ThreadPoolExecutor.CallerRunsPolicy());
	@Override
	public void run(ApplicationArguments args) {
		log.info("load thread pool start");
		try {
			SensorParseTask sensorParseTask = new SensorParseTask();
			DataStoreTask dataStoreTask = new DataStoreTask();
			AlarmTask alarmTask = new AlarmTask();
			
			//ActionTask actionTask = new ActionTask();
			
			executorService.submit(sensorParseTask);
			executorService.submit(dataStoreTask);
			executorService.submit(alarmTask);
			//executorService.submit(actionTask);
			
		}catch(Exception e) {
			log.error("thread pool exception="+e.getLocalizedMessage());
		}
		log.info("load thread pool end");
	}
	/**
	 * 项目销毁前执行
	 */
	@PreDestroy
	public void destroy() {
		try {
			executorService.shutdown();
			System.out.println("close thread pool---");
		}catch(Exception e) {
			log.error("close thread pool exception="+e.getLocalizedMessage());
		}
	}
}
