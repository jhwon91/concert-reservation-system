package com.hhplus.concert.payment.infrastructure;

import com.hhplus.concert.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
}
