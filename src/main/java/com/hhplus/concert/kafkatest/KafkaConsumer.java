package com.hhplus.concert.kafkatest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private String receivedMessage;

    @KafkaListener(topics = "test-topic", groupId = "concert-group")
    public void listen(String message) {
        this.receivedMessage = message;
        log.info("Received message: {}", message);
        latch.countDown();
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
