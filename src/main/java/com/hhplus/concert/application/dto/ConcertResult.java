package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.*;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ConcertResult {

    @Builder
    public record ConcertDTO (
            Long concert_id,
            String title
    ) { }

    @Builder
    public record ConcertDetailDTO (
            Long concert_detail_id,
            Long concert_id,
            Long max_seat,
            Long price,
            LocalDate concert_date
    ) {
        public static ConcertDetailDTO from(ConcertDetails concertDetails) {
            return ConcertDetailDTO.builder()
                    .concert_detail_id(concertDetails.getId())
                    .concert_id(concertDetails.getConcertId())
                    .max_seat(concertDetails.getMaxSeat())
                    .price(concertDetails.getPrice())
                    .concert_date(concertDetails.getConcertDate())
                    .build();
        }
    }

    @Builder
    public record SeatDTO(
            Long seat_id,
            Integer seat_number,
            SeatStatus status
    ) {
        public static SeatDTO from (Seat seat) {
            return SeatDTO.builder()
                    .seat_id(seat.getId())
                    .seat_number(seat.getSeatNumber())
                    .status(seat.getStatus())
                    .build();
        }
    }

    @Builder
    public record ConcertAvailableDates (
            Long concert_id,
            String concert_title,
            List<ConcertDetailDTO> concert_detail
    ) {
        public static ConcertAvailableDates from(Concert concert, List<ConcertDetails> concertDetails) {
            return ConcertAvailableDates.builder()
                    .concert_id(concert.getId())
                    .concert_title(concert.getTitle())
                    .concert_detail(concertDetails.stream()
                            .map(ConcertDetailDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public record ConcertAvailableSeats (
            Long concert_id,
            String concert_title,
            Long concert_detail_id,
            LocalDate concert_date,
            Long max_seat,
            List<SeatDTO> seats
    ) {
        public static ConcertAvailableSeats from(Concert concert, ConcertDetails concertDetails, List<Seat> seats){
            return ConcertResult.ConcertAvailableSeats.builder()
                    .concert_id(concert.getId())
                    .concert_title(concert.getTitle())
                    .concert_detail_id(concertDetails.getId())
                    .concert_date(concertDetails.getConcertDate())
                    .max_seat(concertDetails.getMaxSeat())
                    .seats(seats.stream()
                            .map(SeatDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public record ConcertReservation (
            Long reservation_id,
            Long user_id,
            String user_name,
            Long concert_id,
            String concert_name,
            Long concert_detail_id,
            LocalDate concert_date,
            Long seat_id,
            Integer seat_number,
            SeatStatus status,
            LocalDateTime reservation_at
    ) {
        public static ConcertReservation from(
                Reservation reservation,
                User user,
                ConcertDetails concertDetails,
                Concert concert,
                Seat seat
        ) {
            return ConcertReservation.builder()
                    .reservation_id(reservation.getId())
                    .user_id(reservation.getUserId())
                    .user_name(user.getName())
                    .concert_id(concertDetails.getConcertId())
                    .concert_name(concert.getTitle())
                    .concert_detail_id(concertDetails.getId())
                    .concert_date(concertDetails.getConcertDate())
                    .seat_id(seat.getId())
                    .seat_number(seat.getSeatNumber())
                    .status(seat.getStatus())
                    .reservation_at(reservation.getReservationAt())
                    .build();
        }
    }
}
