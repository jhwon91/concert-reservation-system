package com.hhplus.concert.queue.domain.repository;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueTokenRepository {
    QueueToken save(QueueToken queueToken);
    Optional<QueueToken> findByTokenValue(String tokenValue);
    Optional<QueueToken> findByUserId(UUID userId);
    long countByStatus(QueueTokenStatus status);
    List<QueueToken> findByStatusOrderByPositionAsc(QueueTokenStatus status);
    List<QueueToken> findExpiredTokens(LocalDateTime now);
}
