package com.university.springboot_petroleum.service.impl;

import java.io.File;
//import java.io.FileWriter;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.university.springboot_petroleum.service.DataExportCSVService;
import com.university.springboot_petroleum.service.DataQueryService;
import com.university.springboot_petroleum.util.CSVUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DataExportCSVServiceImpl implements DataExportCSVService{

	@Autowired
	DataQueryService dataQueryService;
	
	@Value("${spring.export.csvpath}")
	private String filePath;
	
	@Value("${spring.export.pyodpath}")
	private String pyodPath;
	
	@Value("${spring.export.pyodfile}")
	private String pyodFile;
	
	@Value("${spring.export.sarimapath}")
	private String sarimaPath;
	
	@Value("${spring.export.sarimafile}")
	private String sarimaFile;
	
	@Value("${spring.export.autoarimapath}")
	private String autoarimaPath;
	
	@Value("${spring.export.autoarimafile}")
	private String autoarimaFile;
	
	@Override
	public void downLoadCSV(HttpServletResponse response,String fileName,String start,String end) {
		try {
			log.info("DataExportCSVServiceImpl enter");
			//查询数据
			List<Map<String,Object>> dataList = dataQueryService.qryData(start, end);
			//log.info("data list query ok size="+dataList.size());
			//导出数据的目录
			File fileDir = new File(filePath);
			if(fileDir.exists() == false) {
				log.info("data export dictory is no exist make dir");
				fileDir.mkdir();
			}
			//创建导出数据的文件
			//String fileNameTemp = filePath+File.separator+fileName + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".csv";
			/*
			File file = new File(fileNameTemp);
			
			CSVPrinter printer = null;
			if(!file.exists()) {
				//file.createNewFile();
				//如果文件不存在，则创建test.csv文件，并在第一行写入字段
	            printer = CSVFormat.EXCEL.print(new FileWriter(file));
			}*/
			String[] head= {"time","液体流量计1","气体流量计2","温度传感器3","温度传感器4","液位传感器5","压力传感器6","液体流量计7","气体流量计8","温度传感器9","温度传感器10","压力传感器11","液位传感器12","气体流量计13","温度传感器14","压力传感器15","液体流量计16","温度传感器18","压力传感器19","液体流量计20","液位传感器21","温度传感器22","压力传感器23","温度传感器24","压力传感器25","液体流量计26","液位传感器27","压力传感器28","温度传感器29","温度传感器30","压力传感器31","液体流量计32"};
			File file = CSVUtil.makeCSV(fileName, filePath, head, dataList);
			CSVUtil.downloadFile(response, file);
		} catch (Exception e) {
			log.error("downLoad csv exception="+e.getLocalizedMessage());
		}
	}
	
	@Override
	public boolean exportPyod(String sensorName1,String sensorName2, String start,String end) {
		boolean result = false;
		try {
			log.info("exportPyod file enter");
			//查询数据
			List<Map<String,Object>> dataList = dataQueryService.qrySensorData(sensorName1, sensorName2, start, end);
			//log.info("data list query ok size="+dataList.size());
			//导出数据的目录
			File fileDir = new File(pyodPath);
			if(fileDir.exists() == false) {
				log.info("data export dictory is no exist make dir");
				fileDir.mkdir();
			}
			List<String> headList = new ArrayList<>();
			headList.add("time");
			headList.add(sensorName1);
			headList.add(sensorName2);
			String[] head= headList.toArray(new String[headList.size()]);
			List<String> headShow = new ArrayList<>();
			headShow.add("timestamp");
			headShow.add(sensorName1);
			headShow.add(sensorName2);
			String[] head2 = headShow.toArray(new String[headShow.size()]);
			File file = CSVUtil.makeCSVHead(pyodFile, pyodPath, head,head2,dataList);
			if(file.exists()) {
				result = true;
			}
			
		} catch (Exception e) {
			log.error("export pyod data exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public boolean exportSarima(String sensorName1,String start,String end) {
		boolean result = false;
		try {
			log.info("exportArima file enter");
			//查询数据
			List<Map<String,Object>> dataList = dataQueryService.qrySensorData2(sensorName1,start, end);
			//log.info("data list query ok size="+dataList.size());
			//导出数据的目录
			File fileDir = new File(sarimaPath);
			if(fileDir.exists() == false) {
				log.info("data export dictory is no exist make dir");
				fileDir.mkdir();
			}
			List<String> headList = new ArrayList<>();
			headList.add("time");
			headList.add(sensorName1);
			
			String[] head= headList.toArray(new String[headList.size()]);
			List<String> headShow = new ArrayList<>();
			headShow.add("timestamp");
			headShow.add(sensorName1);
			
			String[] head2 = headShow.toArray(new String[headShow.size()]);
			File file = CSVUtil.makeCSVHead(sarimaFile, sarimaPath, head,head2,dataList);
			if(file.exists()) {
				result = true;
			}
			
		} catch (Exception e) {
			log.error("export arima data exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public boolean exportAutoArima(String sensorName1,String start,String end) {
		boolean result = false;
		try {
			log.info("exportAutoArima file enter");
			//查询数据
			List<Map<String,Object>> dataList = dataQueryService.qrySensorData2(sensorName1,start, end);
			//log.info("data list query ok size="+dataList.size());
			//导出数据的目录
			File fileDir = new File(autoarimaPath);
			if(fileDir.exists() == false) {
				log.info("data export dictory is no exist make dir");
				fileDir.mkdir();
			}
			List<String> headList = new ArrayList<>();
			headList.add("time");
			headList.add(sensorName1);
			
			String[] head= headList.toArray(new String[headList.size()]);
			List<String> headShow = new ArrayList<>();
			headShow.add("timestamp");
			headShow.add(sensorName1);
			
			String[] head2 = headShow.toArray(new String[headShow.size()]);
			File file = CSVUtil.makeCSVHead(autoarimaFile, autoarimaPath, head,head2,dataList);
			if(file.exists()) {
				result = true;
			}
			
		} catch (Exception e) {
			log.error("export autoarima data exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
}
