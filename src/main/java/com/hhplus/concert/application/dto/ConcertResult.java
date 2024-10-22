package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ConcertResult {

    @Builder
    public record Concert (
            Long concert_id,
            String title
    ) { }

    @Builder
    public record ConcertDetails (
            Long concert_detail_id,
            Long concert_id,
            Long max_seat,
            Long price,
            LocalDate concert_date
    ) { }

    @Builder
    public record ConcertAvailableDates (
            Long concert_id,
            String concert_title,
            List<ConcertDetails> concert_detail
    ) { }
}
