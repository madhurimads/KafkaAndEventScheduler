package com.madhu.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class KafkaEventConsumer {

	private String payload;
	
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
    	payload = message;
        System.out.println("Received message: " + payload);
    }
    
    public String getPayload()
    {
    	return payload;
    }
}
