package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.interfaces.dto.SeatInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public record Seat(
            Long seat_id,
            Integer seat_number,
            SeatStatus status
    ) {}

    @Builder
    public record ConcertAvailableDates (
            Long concert_id,
            String concert_title,
            List<ConcertDetails> concert_detail
    ) { }

    @Builder
    public record ConcertAvailableSeats (
            Long concert_id,
            String concert_title,
            Long concert_detail_id,
            LocalDate concert_date,
            Long max_seat,
            List<Seat> seats
    ) { }

    @Builder
    public record ConcertReservation (
            Long reservation_id,
            Long user_id,
            String user_name,
            Long concert_id,
            String concert_name,
            Long concert_detail_id,
            String concert_date,
            Long seat_id,
            Integer seat_number,
            String status,
            LocalDateTime reservation_at
    ) { }
}
