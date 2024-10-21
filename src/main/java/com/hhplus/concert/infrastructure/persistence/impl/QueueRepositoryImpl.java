package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import com.hhplus.concert.infrastructure.persistence.JpaQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class QueueRepositoryImpl implements QueueRepository {
    private final JpaQueueRepository jpaQueueRepository;

    @Autowired
    public QueueRepositoryImpl(JpaQueueRepository jpaQueueRepository) {
        this.jpaQueueRepository = jpaQueueRepository;
    }

    @Override
    public Queue save(Queue queue) {
        return jpaQueueRepository.save(queue);
    }

    @Override
    public List<Queue> findByUserId(long userId) {
        return jpaQueueRepository.findByUserId(userId);
    }

    @Override
    public long countByStatus(TokenStatus status) {
        return jpaQueueRepository.countByStatusWithLock(status);
    }

    @Override
    public Queue findByToken(String token) {
        return jpaQueueRepository.findByToken(token);
    }

    @Override
    public int countByIdLessThanAndStatus(Long id, TokenStatus status) {
        return jpaQueueRepository.countByIdLessThanAndStatus(id, status);
    }

}