package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaQueueRepository extends JpaRepository<Queue, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.status = :status")
    long countByStatusWithLock(@Param("status") TokenStatus status);

    List<Queue> findByUserId(Long userId);
    Optional<Queue> findByToken(UUID Token);
    int countByIdLessThanAndStatus(Long id, TokenStatus status);
    boolean existsByToken(UUID token);

    List<Queue> findByStatus(TokenStatus status, Pageable pageable);
}
