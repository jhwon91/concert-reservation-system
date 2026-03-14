package com.hhplus.concert.payment.application.dto;

import com.hhplus.concert.payment.domain.PaymentMethod;

import java.util.UUID;

public record ProcessPaymentCommand(UUID userId, UUID reservationId, PaymentMethod paymentMethod) {
}
