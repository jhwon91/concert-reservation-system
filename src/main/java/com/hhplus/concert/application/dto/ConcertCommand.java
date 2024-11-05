package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class ConcertCommand {

    @Builder
    public record AvailableConcertDates(
            UUID token,
            Long concertId
    ){

    }

    @Builder
    public record AvailableConcertSeats(
            UUID token,
            Long concertId,
            Long concertDetailId
    ){}

    @Builder
    public record ReserveSeat(
            UUID token,
            Long userId,
            Long concertDetailId,
            Long seatId
    ){}
}
