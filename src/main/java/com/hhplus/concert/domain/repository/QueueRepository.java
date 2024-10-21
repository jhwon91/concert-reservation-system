package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    Queue save(Queue queue);
    List<Queue> findByUserId(long id);
    long countByStatus(TokenStatus status);
    Queue findByToken(String token);
    int countByIdLessThanAndStatus(Long id, TokenStatus status);
}