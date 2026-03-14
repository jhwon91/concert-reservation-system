package com.hhplus.concert.reservation.infrastructure;

import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final JpaReservationRepository jpaReservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(UUID reservationId) {
        return jpaReservationRepository.findById(reservationId);
    }

    @Override
    public List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status) {
        return jpaReservationRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Reservation> findExpiredPendingReservations(LocalDateTime now) {
        return jpaReservationRepository.findExpiredPendingReservations(now);
    }
}
