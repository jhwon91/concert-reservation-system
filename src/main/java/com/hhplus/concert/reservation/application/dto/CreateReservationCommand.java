package com.hhplus.concert.reservation.application.dto;

import java.util.UUID;

public record CreateReservationCommand(UUID userId, UUID seatId, String tokenValue) {
}
