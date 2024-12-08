package com.madhu.kafka.controller;

import com.madhu.kafka.producer.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {
	@Value(value = "${kafka.topic.name}")
	private String kafkaTopicName;
	
    @Autowired
    private KafkaEventProducer eventProducer;
    
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String message) {
    	eventProducer.sendTestEvent(kafkaTopicName, message);
        return ResponseEntity.ok("Message sent successfully");
    }
}
