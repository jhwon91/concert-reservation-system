package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Payment;
import com.hhplus.concert.domain.repository.PaymentRepository;
import com.hhplus.concert.infrastructure.persistence.JpaPaymentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }
}
