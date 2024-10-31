package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.application.dto.ConcertCommand;
import com.hhplus.concert.application.dto.ConcertResult;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConcertDTO {

    @Builder
    public record ConcertDetailDTO(
            Long concert_detail_id,
            LocalDate date
    ) {
        public static ConcertDetailDTO from(ConcertResult.ConcertDetailDTO result) {
            return ConcertDetailDTO.builder()
                    .concert_detail_id(result.concert_detail_id())
                    .date(result.concert_date())
                    .build();
        }
    }

    @Builder
    public record SeatDTO(
            Long seat_id,
            Integer seat_number,
            SeatStatus status
    ) {
        public static SeatDTO from (ConcertResult.SeatDTO result) {
            return SeatDTO.builder()
                    .seat_id(result.seat_id())
                    .seat_number(result.seat_number())
                    .status(result.status())
                    .build();
        }
    }

    @Builder
    public record ConcertDatesResponseDTO(
            Long concert_id,
            String concert_title,
            List<ConcertDetailDTO> concert_detail
    ) {
        public static ConcertDatesResponseDTO from (ConcertResult.ConcertAvailableDates result){
            return ConcertDatesResponseDTO.builder()
                    .concert_id(result.concert_id())
                    .concert_title(result.concert_title())
                    .concert_detail(result.concert_detail().stream()
                            .map(ConcertDetailDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public record SeatResponseDTO (
            Long concert_id,
            String concert_title,
            Long concert_detail_id,
            LocalDate concert_date,
            Long max_seat,
            List<SeatDTO> seats
    ) {
        public static SeatResponseDTO from (ConcertResult.ConcertAvailableSeats result) {
            return SeatResponseDTO.builder()
                    .concert_id(result.concert_id())
                    .concert_title(result.concert_title())
                    .concert_detail_id(result.concert_detail_id())
                    .concert_date(result.concert_date())
                    .max_seat(result.max_seat())
                    .seats(result.seats().stream()
                            .map(SeatDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public record ReservationResponseDTO(
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
        public static ReservationResponseDTO from (ConcertResult.ConcertReservation result){
            return ReservationResponseDTO.builder()
                    .reservation_id(result.reservation_id())
                    .user_id(result.user_id())
                    .user_name(result.user_name())
                    .concert_id(result.concert_id())
                    .concert_name(result.concert_name())
                    .concert_detail_id(result.concert_detail_id())
                    .concert_date(result.concert_date())
                    .seat_id(result.seat_id())
                    .seat_number(result.seat_number())
                    .status(result.status())
                    .reservation_at(result.reservation_at())
                    .build();
        }
    }

    public record ConcertDatesRequestDTO(
            @RequestHeader("Authorization") UUID token,
            @PathVariable("concert_id") Long concertId
    ) {
        public ConcertCommand.availableConcertDates toCommand() {
            return ConcertCommand.availableConcertDates.builder()
                    .token(token)
                    .concertId(concertId)
                    .build();
        }
    }

    public record ConcertSeatsRequestDTO(
            @RequestHeader("Authorization") UUID token,
            @RequestParam("concert_id") Long concertId,
            @RequestParam("concert_detail_id") Long concertDetailId
    ) {
        public ConcertCommand.availableConcertSeats toCommand() {
            return ConcertCommand.availableConcertSeats.builder()
                    .token(token)
                    .concertId(concertId)
                    .concertDetailId(concertDetailId)
                    .build();
        }
    }

    @Builder
    public record ReservationRequestDTO(
            Long userId,
            Long concertDetailId,
            Long seatId
    ) {
        public ConcertCommand.reserveSeat toCommand(UUID token) {
            return ConcertCommand.reserveSeat.builder()
                    .userId(userId)
                    .concertDetailId(concertDetailId)
                    .seatId(seatId)
                    .token(token)
                    .build();
        }
    }
}
