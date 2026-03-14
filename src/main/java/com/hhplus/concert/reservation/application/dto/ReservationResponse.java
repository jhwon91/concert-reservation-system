package com.hhplus.concert.reservation.application.dto;

import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        UUID seatId,
        ReservationStatus status,
        LocalDateTime expiresAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getSeatId(),
                reservation.getStatus(),
                reservation.getExpiresAt()
        );
    }
}
