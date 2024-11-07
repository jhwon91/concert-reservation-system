package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueRepository {
    Queue save(Queue queue);
    List<Queue> findByUserId(long id);
    long countByStatus(TokenStatus status);
    Optional<Queue> findByToken(UUID token);
    int countByIdLessThanAndStatus(Long id, TokenStatus status);
    boolean exists (UUID token);
    List<Queue> findByStatus(TokenStatus status, Pageable pageable);
    List<Queue> findActiveQueuesToExpire(TokenStatus status, LocalDateTime expirationTime);
}
