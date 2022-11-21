package com.university.springboot_petroleum.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.university.springboot_petroleum.domain.Task;
import com.university.springboot_petroleum.service.TaskService;
import com.university.springboot_petroleum.util.SpringUtil;

import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
@Slf4j
@Component
public class SystemTask implements SchedulingConfigurer{
    @Autowired
    private TaskService taskService;
	
//	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    private static Map<String,ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>();
    
  //从数据库里取得所有要执行的定时任务
    private List<Task> getAllTasks() throws Exception {
    	List<Task> list= taskService.qryRunningTask();
    	return list;
    }

    static {
        threadPoolTaskScheduler.initialize();
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	 List<Task> tasks= null;
         try {
             tasks = getAllTasks();
         } catch (Exception e) {
             log.error("configureTasks get all task exception="+e.getLocalizedMessage());
         }
         
       //校验数据（这个步骤主要是为了打印日志，可以省略）
         checkDataList(tasks);

         //通过校验的数据执行定时任务
         int count = 0;
         if(tasks.size()>0) {
             for (Task task:tasks) {
                 try {
                     //taskRegistrar.addTriggerTask(getRunnable(task), getTrigger(task));
                     start(task);
                     count++;
                 } catch (Exception e) {
                     log.error("定时任务启动错误:" + task.getBeanName() + ";" + task.getMethodName() + ";" + e.getMessage());
                 }
             }
         }
    }
    
    private static Runnable getRunnable(Task task){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Object obj = SpringUtil.getBean(task.getBeanName());
                    Method method = null;
                    if (StringUtils.isBlank(task.getTaskParams())) {
                    	method = obj.getClass().getMethod(task.getMethodName());
					}else {
						method = obj.getClass().getMethod(task.getMethodName(),String.class);
					}
                    
                    if(StringUtils.isNotEmpty(task.getTaskParams())) {
                    	log.info("getRunnable invoke parameter enter");
                    	method.invoke(obj, task.getTaskParams());
                    	log.info("getRunnable invoke parameter success");
                    }else {
                    	log.info("getRunnable invoke enter");
                    	method.invoke(obj);
                    	log.info("getRunnable invoke success");
					}
                    
                } catch (InvocationTargetException e) {
                    log.error("定时任务启动错误，反射异常:"+task.getBeanName()+";"+task.getMethodName()+";"+ e.getMessage());
                } catch (Exception e) {
                    log.error("getRunnable exception="+e.getLocalizedMessage());
                }
            }
        };
    }
    
    private static Trigger getTrigger(Task task){
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                //将Cron 0/1 * * * * ? 输入取得下一次执行的时间
                CronTrigger trigger = new CronTrigger(task.getCron());
                Date nextExec = trigger.nextExecutionTime(triggerContext);
                return nextExec;
            }
        };

    }
    private List<Task> checkDataList(List<Task> list) {
        String errMsg="";
        for(int i=0;i<list.size();i++){
            if(!checkOneData(list.get(i)).equalsIgnoreCase("success")){
                errMsg+=list.get(i).getTaskName()+";";
                list.remove(list.get(i));
                i--;
            };
        }
        if(!StringUtils.isBlank(errMsg)){
            errMsg="未启动的任务:"+errMsg;
            log.error(errMsg);
        }
        return list;
    }

    
    public static String checkOneData(Task task){
        String result="success";
        Class cal= null;
        try {
        	//log.error("checkOneData class for name start");
            cal = Class.forName(task.getBeanName());
            //log.error("-------"+cal);

            Object obj = SpringUtil.getBean(cal);
            Method method = null;
            if(StringUtils.isBlank(task.getTaskParams())) {
            	method = obj.getClass().getMethod(task.getMethodName());
            }else {
            	method = obj.getClass().getMethod(task.getMethodName(),String.class);
            	log.info("checkOneData method has parameters");
			}
            
            String cron=task.getCron();
            if(StringUtils.isBlank(cron)){
                result="定时任务启动错误，无cron:"+task.getTaskName();
                log.error(result);
            }
        } catch (ClassNotFoundException e) {
            result="定时任务启动错误，找不到类:"+task.getBeanName()+ e.getMessage();
            log.error(result);
        } catch (NoSuchMethodException e) {
            result="定时任务启动错误，找不到方法,方法必须是public:"+task.getBeanName()+";"+task.getMethodName()+";"+ e.getMessage();
            log.error(result);
        } catch (Exception e) {
            log.error("checkOneData exception="+e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * 启动定时任务
     * @param task
     * @param
     */
    public static void start(Task task){

        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(getRunnable(task),getTrigger(task));
        scheduledFutureMap.put(Integer.toString(task.getTaskID()),scheduledFuture);
        log.info("启动定时任务" + task.getTaskID());

    }

    /**
     * 取消定时任务
     * @param task
     */
    public static void cancel(Task task){
        log.info("cancel task enter task="+task.getTaskID());
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(Integer.toString(task.getTaskID()));
        if(scheduledFuture != null) {
        	log.info("start cancel task enter");
        	//取消定时任务
        	scheduledFuture.cancel(true);
        	log.info("cancel task end");
        	//如果任务取消耗时
        	boolean canceled = scheduledFuture.isCancelled();
        	log.info("cancel task is ok?="+canceled);
        	while(!canceled) {        		
        		scheduledFuture.cancel(true);
        		canceled = scheduledFuture.isCancelled();
        		log.info("task id="+task.getTaskID()+" 取消中");
        	}
        }        
        scheduledFutureMap.remove(Integer.toString(task.getTaskID()));
        log.info("取消定时任务" + task.getTaskID()+" size="+scheduledFutureMap.size());

    }

    /**
     * 编辑
     * @param task
     * @param
     */
    public static void reset(Task task){
        log.info("修改定时任务开始" + task.getTaskID());
        cancel(task);
        start(task);
        log.info("修改定时任务结束" + task.getTaskID());
    }

}
