package com.hhplus.concert.reservation.presentation.dto;

import java.util.UUID;

public record CreateReservationRequest(UUID userId, UUID seatId) {
}
