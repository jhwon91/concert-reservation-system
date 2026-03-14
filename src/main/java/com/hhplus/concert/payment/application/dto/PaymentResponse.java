package com.hhplus.concert.payment.application.dto;

import com.hhplus.concert.payment.domain.Payment;
import com.hhplus.concert.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime completedAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCompletedAt()
        );
    }
}
