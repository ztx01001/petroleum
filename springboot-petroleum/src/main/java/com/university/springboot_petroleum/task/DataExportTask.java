package com.university.springboot_petroleum.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


import com.university.springboot_petroleum.service.DataQueryService;
import com.university.springboot_petroleum.util.CSVUtil;
import com.university.springboot_petroleum.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Configuration  //主要用户标识配置类，兼备Component的效果
@EnableScheduling //开启定时任务
public class DataExportTask {
	
	@Autowired
	private DataQueryService dataQueryService;
	
	@Value("${spring.export.csvpath}")
	private String filePath;
	
	//每小时执行一次数据导出
	//@Scheduled(cron="0 0 * * *  ?")
	public void dataExportTask() {
		Date date = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String end = dateFormat.format(date);
		String start = TimeUtil.getBeforHourNow(end);
		try {
			List<Map<String,Object>> dataList = dataQueryService.qryData(start, end);
			if(dataList != null) {
				String[] fileStr = start.split(" ");
				filePath = filePath + File.separator+fileStr[0];
				String fileName = fileStr[1];
				fileName = fileName.replaceAll(":", "-");
				//导出数据的目录
				File fileDir = new File(filePath);
				if(fileDir.exists() == false) {
					log.info("data export dictory is no exist make dir");
					fileDir.mkdir();
				}
				String[] head= {"time","液体流量计1","气体流量计2","温度传感器3","温度传感器4","液位传感器5","压力传感器6","液体流量计7","气体流量计8","温度传感器9","温度传感器10","压力传感器11","液位传感器12","气体流量计13","温度传感器14","压力传感器15","液体流量计16","温度传感器18","压力传感器19","液体流量计20","液位传感器21","温度传感器22","压力传感器23","温度传感器24","压力传感器25","液体流量计26","液位传感器27","压力传感器28","温度传感器29","温度传感器30","压力传感器31","液体流量计32"};
				CSVUtil.makeCSV(fileName, filePath, head, dataList);
			}
		} catch (Exception e) {
			log.error("DataExportTask exception="+e.getLocalizedMessage());
		}
	}

}
