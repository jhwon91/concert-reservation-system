package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.ConcertFacade;
import com.hhplus.concert.interfaces.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ConcertController {

    private final ConcertFacade concertFacade;

    @Autowired
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
    public ConcertDTO.SeatResponseDTO getAvailableSeats(
            @RequestHeader("Authorization") UUID token,
            @RequestParam("concert_id") Long concertId,
            @RequestParam("concert_detail_id") Long concertDetailId
    ) {
        return ConcertDTO.SeatResponseDTO.from(
                concertFacade.getAvailableSeats(
                        ConcertDTO.ConcertSeatsRequestDTO.builder()
                                .token(token)
                                .concertId(concertId)
                                .concertDetailId(concertDetailId)
                                .build().toCommand()
                )
        );
    }

    /**
     * 5. 좌석 예약
     */
    @PostMapping("/reservations")
    public ConcertDTO.ReservationResponseDTO reserveSeat (ConcertDTO.ReservationRequestDTO request) {
        return ConcertDTO.ReservationResponseDTO.from(concertFacade.reserveSeat(request.toCommand()));
    }
}
