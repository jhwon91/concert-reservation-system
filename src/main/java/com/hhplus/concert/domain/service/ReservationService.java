package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation enterReservation(User user, ConcertDetails concertDetails, Seat seat){
        return Reservation.builder()
                .concert_detail_id(concertDetails.getId())
                .user_id(user.getId())
                .seat_id(seat.getId())
                .status(null)
                .reservation_at(LocalDateTime.now())
                .build();
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation getReservation(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약이 존재하지 않습니다."));
    }

    // 현재이 예약 시간보다 5분 이상 지났다면 결제 불가
    public void checkReservationStatus(Reservation reservation, LocalDateTime now) {
        if (Duration.between(reservation.getReservation_at(), now).toMinutes() >= 5) {
            throw new IllegalArgumentException("결제 가능 시간이 지났습니다.");
        }
    }
}
