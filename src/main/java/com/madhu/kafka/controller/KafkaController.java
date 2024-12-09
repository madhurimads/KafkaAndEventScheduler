package com.madhu.kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.madhu.kafka.producer.KafkaEventProducer;

@RestController
public class KafkaController {
	@Value(value = "${kafka.topic.name}")
	private String kafkaTopicName;
	
    @Autowired
    private KafkaEventProducer eventProducer;
    
    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
    	eventProducer.sendTestEvent(kafkaTopicName, message);
    	return "Message sent successfully";
    }
}
