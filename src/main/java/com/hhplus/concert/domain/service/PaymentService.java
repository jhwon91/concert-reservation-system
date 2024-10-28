package com.hhplus.concert.domain.service;


import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.Payment;
import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment enterReservation(User user, Reservation reservation, ConcertDetails concertDetails){
        return Payment.builder()
                .user_id(user.getId())
                .reservation_id(reservation.getId())
                .amount(concertDetails.getPrice())
                .build();
    }

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }
}
