package com.madhu.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.madhu.kafka.transaction.Transaction;

@Component
public class KafkaEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTestEvent(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
    
    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplateTransaction;

    public void sendTestEventTransaction(String topic, Transaction message) {
    	kafkaTemplateTransaction.send(topic, message);
    }
}