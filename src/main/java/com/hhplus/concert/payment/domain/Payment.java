package com.hhplus.concert.payment.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(nullable = false)
    private UUID reservationId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime completedAt;

    public Payment(UUID reservationId, UUID userId, BigDecimal amount, PaymentMethod method) {
        this.paymentId = UUID.randomUUID();
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new CoreException(ErrorType.PAYMENT_ALREADY_COMPLETED, this.status);
        }
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new CoreException(ErrorType.PAYMENT_ALREADY_COMPLETED, this.status);
        }
        this.status = PaymentStatus.FAILED;
    }
}
