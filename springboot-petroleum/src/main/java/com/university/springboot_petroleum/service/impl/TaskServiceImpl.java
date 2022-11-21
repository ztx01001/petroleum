package com.university.springboot_petroleum.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;


import com.university.springboot_petroleum.domain.Task;
import com.university.springboot_petroleum.service.TaskService;
import com.university.springboot_petroleum.util.Constants;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class TaskServiceImpl implements TaskService{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
    public void add(Task task) throws Exception {

        task.setIsRunning(Constants.Stoped);
        task.setIsDeleted(Constants.Living);
        
        String sql ="INSERT INTO t_timetask(task_name,cron,bean_name,method_name,task_params,discription,create_time,is_running,is_deleted,task_type) VALUES(?,?,?,?,?,?,?,?,?,?);";
        try {
        	jdbcTemplate.update(sql,new PreparedStatementSetter() {
        		@Override
				 public void setValues(PreparedStatement ps) throws SQLException {
        			ps.setString(1, task.getTaskName());
        			ps.setString(2, task.getCron());
        			ps.setString(3, task.getBeanName());
        			ps.setString(4, task.getMethodName());
        			if(task.getTaskParams() == null || task.getTaskParams().isEmpty()) {
        				ps.setString(5, "");
        			}else {
						ps.setString(5, task.getTaskParams());
					}
        			if((task.getDiscription()== null) || (task.getDiscription().isEmpty())) {
        				ps.setString(6, "");
        			}else {
						ps.setString(6, task.getDiscription());
					}
        			Date date = new Date();
					Timestamp timeStamp = new Timestamp(date.getTime());
					ps.setTimestamp(7, timeStamp);
        			ps.setInt(8, task.getIsRunning());
        			ps.setInt(9, task.getIsDeleted());
        			ps.setString(10, task.getTaskType());
        		}
        	});
		} catch (Exception e) {
			log.error("TaskServiceImpl add task exception="+e.getLocalizedMessage());
		}
        
    }
	@Override
	public List<Task> listTask(){
		log.info("list task enter");
		String sql ="SELECT task_id,task_name,cron,task_type,bean_name,method_name,task_params,create_time,discription,is_running from t_timetask WHERE is_deleted = 0;";
		List<Task> taskList = new ArrayList<>();
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int taskID = rows.getInt("task_id");
				String taskName = rows.getString("task_name");
				String cron = rows.getString("cron");
				String taskType = rows.getString("task_type");
				String beanName = rows.getString("bean_name");
				String methodName = rows.getString("method_name");
				String taskParams = rows.getString("task_params");
				Timestamp createTime = rows.getTimestamp("create_time");
				String discription = rows.getString("discription");
				int isRunning = rows.getInt("is_running");
				if(taskParams == null || taskParams.isEmpty()) {
					taskParams = "";
				}
				if(cron == null || cron.isEmpty()) {
					cron = "";
				}
				Task task = new Task(taskID,taskName,cron,beanName,methodName,taskParams);
				if(createTime != null) {
					String createTimeStr = df.format(createTime);					
					task.setCreat_time(createTimeStr);
				}else {
					task.setCreat_time("");
				}
				task.setTaskType(taskType);
				task.setDiscription(discription);
				task.setIsRunning(isRunning);
				taskList.add(task);
			}
			
		} catch (Exception e) {
			log.error("qry Task exception="+e.getLocalizedMessage());
		}
		log.info("list task end tasksize="+taskList.size());
		return taskList;
	}
	
	@Override
	public List<Task> qryRunningTask(){
		String sql ="SELECT task_id,task_name,cron,bean_name,method_name,task_params from t_timetask WHERE is_running =1 and is_deleted = 0;";
		List<Task> taskList = new ArrayList<>();
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
			while(rows.next()) {
				int taskID = rows.getInt("task_id");
				String taskName = rows.getString("task_name");
				String cron = rows.getString("cron");
				String beanName = rows.getString("bean_name");
				String methodName = rows.getString("method_name");
				String taskParams = rows.getString("task_params");
				if(taskParams == null || taskParams.isEmpty()) {
					taskParams = "";
				}
				if(cron == null || cron.isEmpty()) {
					cron = "";
				}
				Task task = new Task(taskID,taskName,cron,beanName,methodName,taskParams);
				taskList.add(task);
			}
			
		} catch (Exception e) {
			log.error("qryRunningTask exception="+e.getLocalizedMessage());
		}
		return taskList;
	}
	
	@Override
	public Task qryTaskByID(int taskID) {
		String sql ="SELECT task_name,cron,task_type,bean_name,method_name,task_params,is_running from t_timetask WHERE task_id=?;";
		Task task = null;
		try {
			SqlRowSet rows = jdbcTemplate.queryForRowSet(sql,taskID);
			while (rows.next()) {
				//int taskID = rows.getInt("task_id");
				String taskName = rows.getString("task_name");
				String cron = rows.getString("cron");
				String taskType = rows.getString("task_type");
				String beanName = rows.getString("bean_name");
				String methodName = rows.getString("method_name");
				String taskParams = rows.getString("task_params");
				int isRunning = rows.getInt("is_running");
				if(taskParams == null || taskParams.isEmpty()) {
					taskParams = "";
				}
				if(cron == null || cron.isEmpty()) {
					cron = "";
				}
				task = new Task(taskID,taskName,cron,beanName,methodName,taskParams);
				task.setIsRunning(isRunning);
				task.setTaskType(taskType);
			}
		} catch (Exception e) {
			log.error("");
		}
		return task;
	}
	
	@Override
	public boolean deleteTask(int taskID) {
		log.info("deleteTask taskid="+taskID);
		String sql = "UPDATE t_timetask SET is_deleted = 1 WHERE task_id=?";	
		boolean result = false;
		try {
			int rows = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			    @Override
			    public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        preparedStatement.setInt(1, taskID);			        
			    }
			});
			if(rows > 0) {
				result = true;
			}
		} catch (Exception e) {
			log.error("deleteTask taskid exception="+e.getLocalizedMessage());
		}
		return result;
	}
    
	@Override
	public boolean updateTaskStatus(int taskID,int isRunning) {
		log.info("update task status taskid="+taskID);
		String sql = "UPDATE t_timetask SET is_running =? WHERE task_id=?";	
		boolean result = false;
		try {
			int rows = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			    @Override
			    public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        preparedStatement.setInt(1, isRunning);	
			        preparedStatement.setInt(2, taskID);
			    }
			});
			if(rows > 0) {
				result = true;
			}
		} catch (Exception e) {
			log.error("update task status taskid exception="+e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public boolean updateTask(Task task) {
		int taskID = task.getTaskID();
		log.info("edit task taskid="+taskID);
		String sql = "UPDATE t_timetask SET task_name=?,cron=?,discription=?,update_time=? WHERE task_id=?";
		boolean result = false;
		try {
			int rows = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			    @Override
			    public void setValues(PreparedStatement preparedStatement) throws SQLException {
			        preparedStatement.setString(1, task.getTaskName());	
			        preparedStatement.setString(2, task.getCron());
			        preparedStatement.setString(3, task.getDiscription());
			        Date date = new Date();
					Timestamp timeStamp = new Timestamp(date.getTime());
					preparedStatement.setTimestamp(4, timeStamp);
					preparedStatement.setInt(5, taskID);
			    }
			});
			if(rows > 0) {
				result = true;
			}
		} catch (Exception e) {
			log.error("update task taskid exception="+e.getLocalizedMessage());
		}
		return result;
	}
}
