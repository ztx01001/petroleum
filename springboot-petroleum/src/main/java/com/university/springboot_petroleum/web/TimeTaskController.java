package com.university.springboot_petroleum.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.university.springboot_petroleum.domain.Task;
import com.university.springboot_petroleum.exception.DataEmptyException;
import com.university.springboot_petroleum.exception.TaskException;
import com.university.springboot_petroleum.service.TaskService;
import com.university.springboot_petroleum.task.SystemTask;
import com.university.springboot_petroleum.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TimeTaskController {

	@Autowired
	private TaskService taskService;
	
	
	@PostMapping(value = "/api/task")
	public void addTimeTask(@RequestBody Map map) {
		log.info("/api/task post enter");
		try {
			String taskName = String.valueOf(map.get("taskName"));
			String cron = String.valueOf(map.get("cron"));
			String taskType = String.valueOf(map.get("taskType"));
			String beanName = null;
			String methodName = null;
			String taskParams = null;
			if(taskType.equalsIgnoreCase(Constants.DataCollect)) {
				beanName="com.university.springboot_petroleum.task.CollectDataTask";
				methodName="run";
				taskParams="";
			}else if (taskType.equalsIgnoreCase(Constants.DataExport)) {
				beanName="com.university.springboot_petroleum.task.DataExportTask2";
				methodName="dataExportTask";
				taskParams="1";
			}			
			Object remark = map.get("discription");
			String discription = null;
			if(remark == null) {
				discription = "";
			}else {
				discription = String.valueOf(remark);
			}
			Task task = new Task(taskName,cron,beanName,methodName,taskParams,discription);
			task.setTaskType(taskType);
			taskService.add(task);
		} catch (Exception e) {
			log.error("/api/task add exception="+e.getLocalizedMessage());
			throw new TaskException("add task exception");
		}
	}
	/*
	 * 查询task
	 **/
	@GetMapping(value = "/api/task")
	public List<Task> listTask(){
		List<Task> resultList = taskService.listTask();
		if(resultList != null && (resultList.isEmpty() == false)) {
			return resultList;
		}else {
			throw new DataEmptyException("no task info");
		}
	}
	
	
	/*
	 * 删除task
	 **/
	@DeleteMapping(value = "/api/task")
	public void deleteTimeTask(@RequestParam("taskID") int taskID) {
		try {
			taskService.deleteTask(taskID);
			Task task = taskService.qryTaskByID(taskID);
			if(task.getIsRunning() == Constants.Running) {
				SystemTask.cancel(task);
				//更新表数据
				taskService.updateTaskStatus(taskID, Constants.Stoped);
			}
		} catch (Exception e) {
			throw new TaskException("delete task exception");
		}
	}
	
	/**
	 * 更新task
	 */
	@PutMapping(value = "/api/task")
	public boolean updateTask(@RequestBody Map map) {
		log.info("/api/task update task enter");
		boolean result = false;
		int taskID = Integer.parseInt(map.get("taskID").toString());
		String taskName = String.valueOf(map.get("taskName"));
		String cron = String.valueOf(map.get("cron"));		
		String discription = String.valueOf(map.get("discription"));
		Task task = new Task(taskID, taskName, cron);
		
		task.setDiscription(discription);
		result = taskService.updateTask(task);
		Task task2 = taskService.qryTaskByID(taskID);
		int isRunning = task2.getIsRunning();
		if (isRunning == Constants.Running) {
			SystemTask.reset(task2);
		}
		return result;
	}
	
	/**
	 * 更新task运行状态
	 */
	@PutMapping(value = "/api/taskStatus")
	public boolean updateTaskStatus(@RequestBody Map map) {
		int taskID = Integer.parseInt(map.get("taskID").toString());
		int isRunning = Integer.parseInt(map.get("isRunning").toString());
		taskService.updateTaskStatus(taskID, isRunning);
		Task task = taskService.qryTaskByID(taskID);
		log.info("/api/task taskID="+taskID+" isRunning="+isRunning);
		if(isRunning == Constants.Running) {			
			if(!SystemTask.checkOneData(task).equalsIgnoreCase("success")) {
				return false;
			}else {
				log.info("start taskid enter="+taskID);
				SystemTask.start(task);
			}
			
		}else {
			log.info("cancel taskid enter="+taskID);
			SystemTask.cancel(task);
		}
		return true;
	}
}
