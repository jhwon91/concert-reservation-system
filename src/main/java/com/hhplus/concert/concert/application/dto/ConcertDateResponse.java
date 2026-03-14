package com.hhplus.concert.concert.application.dto;

import com.hhplus.concert.concert.domain.ConcertDate;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertDateResponse(
        UUID concertDateId,
        LocalDate date,
        int availableSeats
) {
    public static ConcertDateResponse from(ConcertDate concertDate) {
        return new ConcertDateResponse(
                concertDate.getConcertDateId(),
                concertDate.getDate(),
                concertDate.getAvailableSeats()
        );
    }
}
