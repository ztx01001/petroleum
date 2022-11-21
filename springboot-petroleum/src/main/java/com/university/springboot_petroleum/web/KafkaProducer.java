package com.university.springboot_petroleum.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class KafkaProducer {

	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
 
    // 发送消息
    @GetMapping("/test/{message}")
    public void sendMessage1(@PathVariable("message") String normalMessage) {
    	log.info("/test/message"+normalMessage);
        kafkaTemplate.send("topic001", normalMessage);
    }
}
