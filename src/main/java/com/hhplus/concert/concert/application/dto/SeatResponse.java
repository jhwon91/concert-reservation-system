package com.hhplus.concert.concert.application.dto;

import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record SeatResponse(
        UUID seatId,
        int seatNumber,
        BigDecimal price,
        SeatStatus status
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getSeatId(),
                seat.getSeatNumber().getValue(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}
