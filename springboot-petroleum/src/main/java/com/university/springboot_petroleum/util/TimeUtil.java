package com.university.springboot_petroleum.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeUtil {

	public static String getUTC(String timeStr) {
		DateFormat dtfUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 注意月份是MM
		try {
			Date date1 = simpleDateFormat.parse(timeStr);
			Calendar ca = Calendar.getInstance();
			ca.setTime(date1);
			ca.add(Calendar.HOUR, -8);// 日期减8小时
			Date dt1 = ca.getTime();
			String reslut = dtfUTC.format(dt1);
			return reslut;
		} catch (ParseException e) {
			log.info("获取UTC时间，并减少8小时异常");
			log.error(e.getMessage(), e);
		}
		return "";
	}
	
	public static String getBeforHourNow(String date) {
		String beforeHour = null;
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			/* HOUR_OF_DAY 指示一天中的小时 */
	        calendar.setTime(df.parse(date));
	        calendar.add(Calendar.HOUR_OF_DAY, -1);
	        beforeHour = df.format(calendar.getTime());
	        
		} catch (Exception e) {
			log.error("getBeforHourNow exception="+e.getLocalizedMessage());
		}
		return beforeHour;		
	}
	
	public static String getBeforHoursNow(String date,int hours) {
		String beforeHour = null;
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			/* HOUR_OF_DAY 指示一天中的小时 */
	        calendar.setTime(df.parse(date));
	        calendar.add(Calendar.HOUR_OF_DAY, -hours);
	        //calendar.add(Calendar.MINUTE, hours);
	        beforeHour = df.format(calendar.getTime());
	        
		} catch (Exception e) {
			log.error("getBeforHourNow exception="+e.getLocalizedMessage());
		}
		return beforeHour;		
	}

	
}
