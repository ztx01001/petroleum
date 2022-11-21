package com.university.springboot_petroleum.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.university.springboot_petroleum.config.InfluxDBConfig;
import com.university.springboot_petroleum.domain.DataStoreInfo;
import com.university.springboot_petroleum.queue.DataStoreQueue;

public class DataStoreTask implements Runnable{

	private static Logger log = LoggerFactory.getLogger(DataStoreTask.class);
	
	private InfluxDBConfig influxDBConfig;

    public DataStoreTask() {
    	this.influxDBConfig = ApplicationContextProvider.getBean(InfluxDBConfig.class);
    }
	
	@Override
	public void run() {
		while (true) {
			try {
				log.info("DataStoreTask start get data");
				DataStoreInfo data = DataStoreQueue.getData();
				log.info("DataStoreTask start insert data");
				influxDBConfig.insert(data.getMeasureMents(), data.getDataMap());
				log.info("DataStoreTask end insert data");
				data.setDataMap(null);
				data = null;
			} catch (Exception e) {
				log.error("DataStoreTask start insert data exception="+e.getLocalizedMessage());
			}			
		}
		
		
	}
}
