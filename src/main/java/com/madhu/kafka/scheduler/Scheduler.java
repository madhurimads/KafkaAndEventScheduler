package com.madhu.kafka.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.madhu.kafka.producer.KafkaEventProducer;

@Component
public class Scheduler {
	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	
	@Value(value = "${kafka.topic.name}")
	private String kafkaTopicName;
	
	@Autowired
	private KafkaEventProducer kafkaProducer;
	
	@Scheduled(cron = "${cronExprEvery15secs}") // Run every 15 seconds
	public void sendTestEvent()
	{
		logger.info("Scheduler---Running every 15 seconds");
		kafkaProducer.sendTestEvent(kafkaTopicName, "EventMessageFromScheduler");
	}
}
