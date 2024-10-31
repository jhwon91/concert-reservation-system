package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.ReservationRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
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

    public Reservation createAndSaveReservation(User user, ConcertDetails concertDetails, Seat seat) {
        Reservation reservation = Reservation.create(user, concertDetails, seat);
        return save(reservation);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation getReservation(long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CoreException(ErrorType.RESERVATION_NOT_FOUND,reservationId));
    }

    // 현재이 예약 시간보다 5분 이상 지났다면 결제 불가
    public void checkReservationStatus(Reservation reservation, LocalDateTime now) {
        if (Duration.between(reservation.getReservationAt(), now).toMinutes() >= 5) {
            throw new CoreException(ErrorType.PAYMENT_TIME_EXPIRED,Duration.between(reservation.getReservationAt(), now).toMinutes());
        }
    }
}
