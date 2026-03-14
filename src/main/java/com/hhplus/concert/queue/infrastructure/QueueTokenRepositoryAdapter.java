package com.hhplus.concert.queue.infrastructure;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QueueTokenRepositoryAdapter implements QueueTokenRepository {

    private final JpaQueueTokenRepository jpaQueueTokenRepository;

    @Override
    public QueueToken save(QueueToken queueToken) {
        return jpaQueueTokenRepository.save(queueToken);
    }

    @Override
    public Optional<QueueToken> findByTokenValue(String tokenValue) {
        return jpaQueueTokenRepository.findByTokenValue(tokenValue);
    }

    @Override
    public Optional<QueueToken> findByUserId(UUID userId) {
        return jpaQueueTokenRepository.findByUserId(userId);
    }

    @Override
    public long countByStatus(QueueTokenStatus status) {
        return jpaQueueTokenRepository.countByStatus(status);
    }

    @Override
    public List<QueueToken> findByStatusOrderByPositionAsc(QueueTokenStatus status) {
        return jpaQueueTokenRepository.findByStatusOrderByPositionAsc(status);
    }

    @Override
    public List<QueueToken> findExpiredTokens(LocalDateTime now) {
        return jpaQueueTokenRepository.findExpiredTokens(now);
    }
}
