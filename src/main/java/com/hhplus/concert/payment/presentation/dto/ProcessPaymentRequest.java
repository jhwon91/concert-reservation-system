package com.hhplus.concert.payment.presentation.dto;

import com.hhplus.concert.payment.domain.PaymentMethod;

import java.util.UUID;

public record ProcessPaymentRequest(UUID userId, UUID reservationId, PaymentMethod paymentMethod) {
}
