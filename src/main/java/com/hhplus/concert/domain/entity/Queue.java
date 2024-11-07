package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.Token;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name= "queue")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID token;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @Column(name = "entered_at")
    private LocalDateTime enteredAt; //활성화된 시점

    @Column(name = "last_requested_at")
    private LocalDateTime lastRequestedAt; //토큰 사용 마지막 요청 시간

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; //토큰 만료 시간

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isWaiting() {
        return this.status == TokenStatus.WAIT;
    }

    public boolean isActive() {
        return this.status == TokenStatus.ACTIVE;
    }

    public void validateActiveStatus() {
        if (!isActive()) {
            throw new CoreException(ErrorType.TOKEN_NOT_ACTIVE, this.token);
        }
    }

    public void validateUserMatch(User user) {
        if (this.userId != user.getId()) {
            throw new CoreException(ErrorType.USER_NOT_MATCHED_TOKEN, user.getId());
        }
    }

    public Queue updateStatus(TokenStatus newStatus, Optional<LocalDateTime> expiredAt) {
        this.status = newStatus;
        this.expiredAt = newStatus == TokenStatus.EXPIRED ? expiredAt.orElse(LocalDateTime.now()) : null;

        this.enteredAt = newStatus == TokenStatus.ACTIVE ? LocalDateTime.now() : this.enteredAt;
        this.lastRequestedAt = newStatus == TokenStatus.ACTIVE ? LocalDateTime.now() : this.lastRequestedAt;

        return this;
    }

    public static Queue createNew(Long userId, TokenStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return Queue.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .status(status)
                .build();
    }
}
