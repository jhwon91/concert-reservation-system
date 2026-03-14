package com.hhplus.concert.reservation.application.dto;

import java.util.UUID;

public record CancelReservationCommand(UUID reservationId, UUID userId) {
}
