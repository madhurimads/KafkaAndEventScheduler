package com.madhu.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.madhu.kafka.producer.KafkaEventProducer;
import com.madhu.kafka.serde.TransactionDeserializer;
import com.madhu.kafka.transaction.Transaction;

@EmbeddedKafka(partitions=1, brokerProperties= {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(locations = "classpath:application-test.yml")
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmbeddedKafkaProducerTestWithTransaction {

    private BlockingQueue<ConsumerRecord<String, Transaction>> transactionRecords;

    private KafkaMessageListenerContainer<String, String> container;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaEventProducer producer;

    //@Value("${embeddedKafka.topic.name}")
    private String embeddedKafkaBrokerTopicName = "embeddedKafkaBrokerTopic";

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(embeddedKafkaBrokerTopicName);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        transactionRecords = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, Transaction>) transactionRecords::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "consumer",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10",
                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TransactionDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
    
    @Test
    void testSendEventTransactionToKafka() throws InterruptedException, JsonProcessingException {
    	String id = "99";
    	Integer taxRate = 28;
    	Transaction trans = new Transaction(id, taxRate);
    	//Send a test event to the embedded kafka broker
        producer.sendTestEventTransaction(embeddedKafkaBrokerTopicName, trans);
        
        // Read the message with a test consumer from Embedded Kafka and assert its properties
        ConsumerRecord<String, Transaction> transRecord = transactionRecords.poll(500, TimeUnit.MILLISECONDS);
        assertNotNull(transRecord);
        assertEquals(embeddedKafkaBrokerTopicName, transRecord.topic());
        assertEquals(id, transRecord.value().getId());
        assertEquals(taxRate, transRecord.value().getTaxRate());
    }
}