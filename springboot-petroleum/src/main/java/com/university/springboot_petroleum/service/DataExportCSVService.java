package com.university.springboot_petroleum.service;

import javax.servlet.http.HttpServletResponse;

public interface DataExportCSVService {

	public void downLoadCSV(HttpServletResponse response,String fileName,String start,String end);
	
	public boolean exportPyod(String sensorName1,String sensorName2,String start,String end);
	
	public boolean exportSarima(String sensorName1,String start,String end);
	
	public boolean exportAutoArima(String sensorName1,String start,String end);
}
