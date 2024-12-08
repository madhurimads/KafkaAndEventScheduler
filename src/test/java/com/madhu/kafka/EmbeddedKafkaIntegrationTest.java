package com.madhu.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.madhu.kafka.consumer.KafkaEventConsumer;
import com.madhu.kafka.producer.KafkaEventProducer;

@SpringBootTest(classes = {KafkaEventConsumer.class, KafkaEventProducer.class, KafkaTemplate.class})
@RunWith(SpringRunner.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", 
		"port=9093",  
		"bootstrapServersProperty=spring.kafka.bootstrap-servers"})
@TestPropertySource(properties = "spring.kafka.consumer.auto-offset-reset = earliest")
class EmbeddedKafkaIntegrationTest {

    @Autowired
    private KafkaEventConsumer consumer;

    @Autowired
    private KafkaEventProducer producer;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${test.topic}")
    private String topic;
    

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() 
      throws Exception {
        String data = "Sending with our own simple KafkaProducer";        
        producer.sendTestEvent(topic, data);       
        assertEquals(consumer.getPayload(),(data));
    }
}
