package com.university.springboot_petroleum.task;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware{

	/*
	 *上下文对象实例 
	 **/
	private static ApplicationContext applicationContext;
	@Override
	public void  setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		this.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
	public static <T> T getBean(String name,Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
}
