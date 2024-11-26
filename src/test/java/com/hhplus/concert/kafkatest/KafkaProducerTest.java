package com.hhplus.concert.kafkatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class KafkaProducerTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Test
    void 카프카_테스트()  throws InterruptedException {
        String testMessage = "Hello Kafka";
        kafkaConsumer.resetLatch();

        kafkaProducer.sendMessage("test-topic", testMessage);
        boolean messageConsumed = kafkaConsumer.getLatch().await(5, TimeUnit.SECONDS);

        assertEquals(true,messageConsumed);
        assertEquals(testMessage, kafkaConsumer.getReceivedMessage());
    }
}