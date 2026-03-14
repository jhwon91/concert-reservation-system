package com.hhplus.concert.payment.domain.service;

import com.hhplus.concert.payment.domain.Payment;
import com.hhplus.concert.payment.domain.PaymentMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentDomainService {

    public Payment createPayment(UUID reservationId, UUID userId, BigDecimal amount, PaymentMethod method) {
        return new Payment(reservationId, userId, amount, method);
    }
}
