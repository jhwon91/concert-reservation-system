package com.hhplus.concert.reservation.domain.repository;

import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(UUID reservationId);
    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    List<Reservation> findExpiredPendingReservations(LocalDateTime now);
}
