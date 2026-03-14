package com.hhplus.concert.queue.infrastructure;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaQueueTokenRepository extends JpaRepository<QueueToken, UUID> {

    Optional<QueueToken> findByTokenValue(String tokenValue);

    Optional<QueueToken> findByUserId(UUID userId);

    long countByStatus(QueueTokenStatus status);

    List<QueueToken> findByStatusOrderByPositionAsc(QueueTokenStatus status);

    @Query("SELECT qt FROM QueueToken qt WHERE qt.status = 'ACTIVE' AND qt.expiresAt < :now")
    List<QueueToken> findExpiredTokens(@Param("now") LocalDateTime now);
}
