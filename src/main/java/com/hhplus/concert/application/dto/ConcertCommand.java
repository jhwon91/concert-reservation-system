package com.hhplus.concert.application.dto;

import lombok.Builder;

public class ConcertCommand {

    @Builder
    public record availableConcertDates(
            String token,
            Long concertId
    ){

    }

    @Builder
    public record availableConcertSeats(
            String token,
            Long concertId,
            Long concertDetailId
    ){}

    @Builder
    public record reserveSeat(
            String token,
            Long userId,
            Long concertDetailId,
            Long seatId
    ){}
}
