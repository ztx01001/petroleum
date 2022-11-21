package com.university.springboot_petroleum.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class TaskDemo {

	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 public void run(){
		 try {
			 System.out.println("*********");
	         System.out.println("TaskDemo is running"+sdf.format(new Date()));
		 }catch (Exception e) {
			log.error("task demo exception="+e.getLocalizedMessage());
		}
	 }
}
