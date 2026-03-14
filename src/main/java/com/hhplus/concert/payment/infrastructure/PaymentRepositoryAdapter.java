package com.hhplus.concert.payment.infrastructure;

import com.hhplus.concert.payment.domain.Payment;
import com.hhplus.concert.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return jpaPaymentRepository.findById(paymentId);
    }
}
