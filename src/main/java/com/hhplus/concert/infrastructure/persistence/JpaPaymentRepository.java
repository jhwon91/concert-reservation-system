package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {

}
