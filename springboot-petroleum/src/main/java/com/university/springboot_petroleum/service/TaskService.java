package com.university.springboot_petroleum.service;

import java.util.List;

import com.university.springboot_petroleum.domain.Task;

public interface TaskService {

//	//列表查询
	public List<Task> listTask();

//    //根据id查询
	public Task qryTaskByID(int taskID);

	// 添加任务
	public void add(Task task) throws Exception;

	// 查询运行的列表
	public List<Task> qryRunningTask();

    //编辑任务  
	public boolean updateTask(Task task);
//
//    void updstatus(Task task)throws Exception;
//
//    void deleteById(String id)throws Exception;
	// 更新任务运行状态
	public boolean updateTaskStatus(int taskID, int isRunning);

	// 删除任务
	public boolean deleteTask(int taskID);

}
