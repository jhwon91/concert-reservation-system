package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.ConcertFacade;
import com.hhplus.concert.interfaces.dto.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ConcertController {

    private final ConcertFacade concertFacade;

    public ConcertController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    /**
     * 3. 예약 가능한 날짜 조회
     */
    @GetMapping("/concert/{concertId}/dates")
    public ConcertDatesResponseDTO getAvailableConcertDates(
            @PathVariable("concert_id") Long concertId,
            @RequestHeader("Authorization") String token) {

        List<ConcertDetail> concertDetails = Arrays.asList(
                new ConcertDetail(1L, "2024-10-01"),
                new ConcertDetail(2L, "2024-10-02")
        );

        return new ConcertDatesResponseDTO(concertId, "콘서트1", concertDetails);
    }

    /**
     * 4. 특정 날짜의 예약 가능한 좌석 조회
     */
    @GetMapping("/reservations/seats")
    public SeatsResponseDTO getAvailableSeats(
            @RequestHeader("Authorization") String token,
            @RequestParam("concert_id") Long concertId,
            @RequestParam("concert_detail_id") Long concertDetailId
            ) {

        List<SeatInfo> seats = Arrays.asList(
                new SeatInfo(1L, 1, "AVAILABLE"),
                new SeatInfo(2L, 2, "UNAVAILABLE")
        );

        return new SeatsResponseDTO(1L, "콘서트1", concertDetailId, null, 50, seats);
    }

    /**
     * 5. 좌석 예약
     */
    @PostMapping("/reservations")
    public ReservationResponseDTO reserveSeat(
            @RequestBody ReservationRequestDTO request,
            @RequestHeader("Authorization") String token) {

        return new ReservationResponseDTO(
                1L,
                request.user_id(),
                "유저1",
                1L,
                "콘서트1",
                request.concert_detail_id(),
                request.concert_date(),
                request.seat_id(),
                1,
                "TEMPORARY_ALLOCATED",
                LocalDateTime.now()
        );
    }
}
