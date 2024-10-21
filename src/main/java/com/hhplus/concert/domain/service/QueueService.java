package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    private static final int MAX_ACTIVE_USERS = 10; // 예시: 최대 활성 사용자 수

    @Autowired
    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }


    @Transactional
    public Queue addToWaitingList(User user) {
        //활성 사용자 확인
        long activeCount = queueRepository.countByStatus(TokenStatus.ACTIVE);

        //사용자 아이디로 대기열의 상태 확인
        List<Queue> userQueueStatus = queueRepository.findByUserId(user.getId());

        //만료 시간이 null 있으면 상태값 반환(wait, active), 없으면 active 생성
        Queue resultQueue = userQueueStatus.stream()
                .filter(queue -> queue.getEnteredAt() == null)
                .findFirst()
                .orElse(null);

        // WAIT이면 position 붙여야함
        if(resultQueue != null) { return resultQueue; }

        Queue queue = new Queue();
        queue.setUserId(user.getId());
        queue.setToken(UUID.randomUUID());
        queue.setEnteredAt(LocalDateTime.now());
        queue.setLastRequestedAt(LocalDateTime.now());

        if (activeCount < MAX_ACTIVE_USERS) {
            //ACTIVE
            queue.setStatus(TokenStatus.ACTIVE);
        } else {
            //WAIT
            queue.setStatus(TokenStatus.WAIT);
        }

        queue = queueRepository.save(queue);

        // WAIT이면 position 붙여야함
        return queue;
    }
}
