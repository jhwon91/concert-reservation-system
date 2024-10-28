package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.ConcertFacade;
import com.hhplus.concert.application.dto.ConcertCommand;
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
    public ConcertDTO.ConcertDatesResponseDTO getAvailableConcertDates(ConcertDTO.ConcertDatesRequestDTO request) {
        return  ConcertDTO.ConcertDatesResponseDTO.from(
                concertFacade.getAvailableConcertDates(request.toCommand())
        );
    }

    /**
     * 4. 특정 날짜의 예약 가능한 좌석 조회
     */
    @GetMapping("/reservations/seats")
    public ConcertDTO.SeatResponseDTO getAvailableSeats(ConcertDTO.ConcertSeatsRequestDTO request) {
        return ConcertDTO.SeatResponseDTO.from(
                concertFacade.getAvailableSeats(request.toCommand())
        );
    }

    /**
     * 5. 좌석 예약
     */
    @PostMapping("/reservations")
    public ConcertDTO.ReservationResponseDTO reserveSeat (
            @RequestHeader("Authorization") String token,
            @RequestBody ConcertDTO.ReservationRequestDTO request) {
        return ConcertDTO.ReservationResponseDTO.from(
                concertFacade.reserveSeat(request.toCommand(token))
        );
    }
}
