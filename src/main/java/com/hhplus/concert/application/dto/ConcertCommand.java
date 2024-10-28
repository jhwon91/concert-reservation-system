package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class ConcertCommand {

    @Builder
    public record availableConcertDates(
            UUID token,
            Long concertId
    ){

    }

    @Builder
    public record availableConcertSeats(
            UUID token,
            Long concertId,
            Long concertDetailId
    ){}

    @Builder
    public record reserveSeat(
            UUID token,
            Long userId,
            Long concertDetailId,
            Long seatId
    ){}
}
