package com.hhplus.concert.payment.domain.repository;

import com.hhplus.concert.payment.domain.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID paymentId);
}
