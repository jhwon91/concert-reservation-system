package com.hhplus.concert.queue.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "queue_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueToken extends BaseEntity {

    private static final int ACTIVE_DURATION_MINUTES = 10;

    @Id
    @Column(name = "queue_token_id")
    private UUID queueTokenId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String tokenValue;

    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueTokenStatus status;

    private int estimatedWaitSec;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime activatedAt;

    private LocalDateTime expiresAt;

    public QueueToken(UUID userId, TokenValue tokenValue, int position, int estimatedWaitSec) {
        this.queueTokenId = UUID.randomUUID();
        this.userId = userId;
        this.tokenValue = tokenValue.getValue();
        this.position = position;
        this.status = QueueTokenStatus.WAITING;
        this.estimatedWaitSec = estimatedWaitSec;
        this.issuedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status != QueueTokenStatus.WAITING) {
            throw new CoreException(ErrorType.QUEUE_TOKEN_INVALID_STATUS, this.status);
        }
        this.status = QueueTokenStatus.ACTIVE;
        this.activatedAt = LocalDateTime.now();
        this.expiresAt = this.activatedAt.plusMinutes(ACTIVE_DURATION_MINUTES);
        this.position = 0;
        this.estimatedWaitSec = 0;
    }

    public void expire() {
        if (this.status == QueueTokenStatus.EXPIRED) {
            throw new CoreException(ErrorType.QUEUE_TOKEN_INVALID_STATUS, this.status);
        }
        this.status = QueueTokenStatus.EXPIRED;
        this.expiresAt = LocalDateTime.now();
    }

    public void updatePosition(int position, int estimatedWaitSec) {
        this.position = position;
        this.estimatedWaitSec = estimatedWaitSec;
    }

    public boolean isActive() {
        return this.status == QueueTokenStatus.ACTIVE
                && this.expiresAt != null
                && this.expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return this.status == QueueTokenStatus.EXPIRED
                || (this.expiresAt != null && this.expiresAt.isBefore(LocalDateTime.now()));
    }
}
