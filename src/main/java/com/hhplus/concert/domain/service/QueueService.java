package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public Queue addToQueue(User user) {
        List<Queue> userQueues = queueRepository.findByUserId(user.getId());

        Optional<Queue> pendingQueue = userQueues.stream()
                .filter(queue -> queue.getExpiredAt() == null)
                .findFirst();

        if (pendingQueue.isPresent()) {
            return pendingQueue.get();
        }

        TokenStatus status = shouldBeActive() ? TokenStatus.ACTIVE : TokenStatus.WAIT;
        Queue newQueue = Queue.createNew(user.getId(), status);

        return queueRepository.save(newQueue);
    }

    private boolean shouldBeActive() {
        return queueRepository.countByStatus(TokenStatus.ACTIVE) < MAX_ACTIVE_USERS;
    }

    public long countActiveUsers() {
        return queueRepository.countByStatus(TokenStatus.ACTIVE);
    }

    public int getQueuePosition(Queue queue) {
        if (!queue.isWaiting()) {
            return 0;
        }
        return queueRepository.countByIdLessThanAndStatus(queue.getId(), queue.getStatus()) + 1;
    }

    public Queue getQueueByToken(UUID token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, token));
    }

    public void validationUserOfToken(Queue queue, User user) {
        queue.validateUserMatch(user);
    }

    public void validateActiveToken(Queue queue){
        queue.validateActiveStatus();
    }

    public Queue changeQueueStatus(Queue queue, TokenStatus status, Optional<LocalDateTime> expiredAtOpt) {
        return queue.updateStatus(status,expiredAtOpt);
    }

    public int getMaxActiveUsers() {
        return MAX_ACTIVE_USERS;
    }
    public Queue save(Queue queue){
        return queueRepository.save(queue);
    }

    public List<Queue> findNextWaitingQueues(long limit) {
        Pageable pageable = PageRequest.of(0,  (int) limit, Sort.by("createdAt").ascending());
        return queueRepository.findByStatus(TokenStatus.WAIT, pageable);
    }

    public List<Queue> findActiveQueuesToExpire(TokenStatus status, LocalDateTime expirationTime) {
        return queueRepository.findActiveQueuesToExpire(status, expirationTime);
    }
}
