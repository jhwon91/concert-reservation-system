package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;

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
}
