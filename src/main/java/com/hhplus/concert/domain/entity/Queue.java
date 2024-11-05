package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime expiredAt; //토큰 사용 마지막 요청 시간


    // 상태 검증 메서드
    public void validateActiveStatus() {
        if (this.status != TokenStatus.ACTIVE) {
            throw new CoreException(ErrorType.TOKEN_NOT_ACTIVE, this.token);
        }
    }
}
