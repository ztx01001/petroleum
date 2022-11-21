package com.university.springboot_petroleum.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Task {

	    private int taskID;
	    private String taskName;        //任务名
	
	    private String cron;        //表达式
	    private String taskType;
	    private int isRunning; //运行状态  1运行  0暂停
	    private String taskParams;    //参数
	    private String beanName;   //实例bean  全路径类名
	    private String methodName; //方法  定时任务要执行的方法  Task里的run方法
	    private String discription; //任务描述
	    
	    private String creat_time;  //创建时间
	    private String update_time; //修改时间
	    private int isDeleted;   //删除标志   1删除 0未删除
	    
	    public Task(int taskID,String taskName,String cron,String beanName,String methodName,String taskParams) {
	    	this.taskID = taskID;
	    	this.taskName = taskName;
	    	this.cron = cron;
	    	this.beanName = beanName;
	    	this.methodName = methodName;
	    	this.taskParams = taskParams;
	    }
	    
	    public Task(String taskName,String cron,String beanName,String methodName,String taskParams,String discription) {
	    	this.taskName = taskName;
	    	this.cron = cron;
	    	this.beanName = beanName;
	    	this.methodName = methodName;
	    	this.taskParams = taskParams;
	    	this.discription = discription;
	    }
	    
	    public Task(int taskID,String taskName,String cron) {
	    	this.taskID = taskID;
	    	this.taskName = taskName;
	    	this.cron =cron;
	    }
}
