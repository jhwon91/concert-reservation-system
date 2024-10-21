package com.hhplus.concert.Integration;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.infrastructure.persistence.impl.QueueRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//@ActiveProfiles("test") 테스트 디비 사용 할 수 있음
@SpringBootTest
@Transactional
public class QueueRepositoryImplIntegrationTest {
    @Autowired
    private QueueRepositoryImpl queueRepository;

    @Test
    public void testFindByUserId() {
        Queue queue1 = new Queue();
        queue1.setUserId(1L);
        queue1.setStatus(TokenStatus.WAIT);
        queue1.setEnteredAt(LocalDateTime.now());
        queueRepository.save(queue1);

        Queue queue2 = new Queue();
        queue2.setUserId(1L);
        queue2.setStatus(TokenStatus.ACTIVE);
        queue2.setEnteredAt(LocalDateTime.now());
        queueRepository.save(queue2);

        List<Queue> queues = queueRepository.findByUserId(1L);


        for (Queue queue : queues) {
            System.out.println(queue);
        }

        assertEquals(2, queues.size());
        assertTrue(queues.stream().allMatch(q -> q.getUserId() == 1L));
    }
}
