package com.university.springboot_petroleum.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class MailService {
	
	@Value("${spring.mail.username}")
	private String from;
	
	// JavaMailSender 在Mail 自动配置类 MailSenderAutoConfiguration 中已经导入，这里直接注入使用即可
    @Autowired
    JavaMailSender javaMailSender;
    
    public void sendMail(String to,String subject,String content) {
    	log.info("MailService send mail to="+to);
    	try {
    		// 简单邮件直接构建一个 SimpleMailMessage 对象进行配置并发送即可
            SimpleMailMessage simpMsg = new SimpleMailMessage();
            simpMsg.setFrom(from);
            simpMsg.setTo(to);
            simpMsg.setSubject(subject);
            simpMsg.setText(content);
            javaMailSender.send(simpMsg);
            log.info("MailService send mail end");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("sendMail exception="+e.getLocalizedMessage());
		}
    }

}
