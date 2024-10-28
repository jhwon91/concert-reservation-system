package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
