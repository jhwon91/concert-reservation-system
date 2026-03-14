package com.hhplus.concert.reservation.infrastructure;

import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiresAt < :now")
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);
}
