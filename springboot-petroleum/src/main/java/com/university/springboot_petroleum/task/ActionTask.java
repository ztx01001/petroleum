package com.university.springboot_petroleum.task;



import java.util.HashMap;
import java.util.Map;
import com.university.springboot_petroleum.domain.AlertActionMessage;
import com.university.springboot_petroleum.queue.AlarmActionQueue;
import com.university.springboot_petroleum.service.impl.MailService;
import com.university.springboot_petroleum.service.impl.SmsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionTask implements Runnable{
	
	
    private MailService mailService = null;
    private SmsService smsService = null;
    
    public ActionTask() {
    	this.mailService = ApplicationContextProvider.getBean(MailService.class);
    	this.smsService = ApplicationContextProvider.getBean(SmsService.class);
    }

	@Override
	public void run() {
		doConsumer();
	}
	
	private void doConsumer() {
		while (true) {
			try {
				log.info("ActionTask enter");
				AlertActionMessage message = AlarmActionQueue.getData();
				if(message != null) {
					String type = message.getType();
					String subject = "alarm ";
					if(type.equalsIgnoreCase("email")) {						
						log.info("ActionTask email enter");
						mailService.sendMail(message.getObject(), subject, message.getContent());
						log.info("ActionTask email end");
					}else if(type.equalsIgnoreCase("phone")) {
						log.info("ActionTask phone enter");
						Map<String, String> template = new HashMap<>();
						template.put("data", message.getContent());
						smsService.sendSms(message.getObject(), subject, template);
						log.info("ActionTask phone end");
					}
				}
			}catch (Exception e) {
				log.error("ActionTask send email exception="+e.getLocalizedMessage());
			}
		}
	}
}
