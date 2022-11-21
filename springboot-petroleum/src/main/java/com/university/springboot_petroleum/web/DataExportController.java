package com.university.springboot_petroleum.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.university.springboot_petroleum.exception.ExportFileException;
import com.university.springboot_petroleum.service.DataExportCSVService;
import com.university.springboot_petroleum.service.SensorInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DataExportController {
	@Autowired
	DataExportCSVService dataExportCSVService;
	@Autowired
	SensorInfoService sensorInfoService;

	@GetMapping("/downloadCsv")
    @ResponseBody
    public void downLoadCSV(HttpServletRequest request, HttpServletResponse response) throws IOException{
		log.info("downloadcsv enter---");
		//下载文件名字
		String fileName = request.getParameter("fileName");
		log.info("filename==="+fileName);
		//数据查询开始时间
		String start = request.getParameter("start");
		//数据查询结束时间
		String end = request.getParameter("end");
		
		try {
			//响应类型
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //response.setCharacterEncoding("GBK");
            //进行下载
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            //fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            log.info("filename==="+fileName);
            //响应的是  .csv 文件的后缀
            response.setHeader("Content-disposition", "attachment;filename=" + fileNameURL+ ".csv");
            dataExportCSVService.downLoadCSV(response, fileName, start, end);
		} catch (Exception e) {
			log.error("download csv exception");
			throw new ExportFileException("download csv file failed");
		}
		
	}
	@GetMapping(value = "/api/export/pyod")
	@ResponseBody
	public boolean exportPYOData(HttpServletRequest request) {
		int sensorID1 = Integer.valueOf(request.getParameter("sensorID1")).intValue();
		int sensorID2 = Integer.valueOf(request.getParameter("sensorID2")).intValue();
		
		//数据查询开始时间
		String start = request.getParameter("start");
		//数据查询结束时间
		String end = request.getParameter("end");
		boolean result = false;
		try {
			String sensorName1 = sensorInfoService.qrySensorNameByID(sensorID1);
			String sensorName2 = sensorInfoService.qrySensorNameByID(sensorID2);
			log.info("sensorName1==="+sensorName1+" name2="+sensorName2);
			result = dataExportCSVService.exportPyod(sensorName1,sensorName2, start, end);
		} catch (Exception e) {
			log.error("exportPYOData exception="+e.getLocalizedMessage());
			throw new ExportFileException("export pyod data failed");
		}
		return result;
	}
	
	@GetMapping(value = "/api/export/sarima")
	@ResponseBody
	public boolean exportSarimaData(HttpServletRequest request) {
		int sensorID1 = Integer.valueOf(request.getParameter("sensorID")).intValue();		
		//数据查询开始时间
		String start = request.getParameter("start");
		//数据查询结束时间
		String end = request.getParameter("end");
		boolean result = false;
		try {
			String sensorName1 = sensorInfoService.qrySensorNameByID(sensorID1);
			
			log.info("sensorName1==="+sensorName1);
			result = dataExportCSVService.exportSarima(sensorName1,start, end);
		} catch (Exception e) {
			log.error("export arima Data exception="+e.getLocalizedMessage());
			throw new ExportFileException("export arima data failed");
		}
		return result;
	}
	
	@GetMapping(value = "/api/export/autoarima")
	@ResponseBody
	public boolean exportAutoarimaData(HttpServletRequest request) {
		int sensorID1 = Integer.valueOf(request.getParameter("sensorID")).intValue();		
		//数据查询开始时间
		String start = request.getParameter("start");
		//数据查询结束时间
		String end = request.getParameter("end");
		boolean result = false;
		try {
			String sensorName1 = sensorInfoService.qrySensorNameByID(sensorID1);
			
			log.info("sensorName1==="+sensorName1);
			result = dataExportCSVService.exportAutoArima(sensorName1,start, end);
		} catch (Exception e) {
			log.error("export arima Data exception="+e.getLocalizedMessage());
			throw new ExportFileException("export arima data failed");
		}
		return result;
	}
}
