package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.ConcertFacade;
import com.hhplus.concert.interfaces.dto.ConcertDatesResponseDTO;
import com.hhplus.concert.interfaces.dto.ConcertDetail;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/reservations/dates/{concert_id}")
    public ConcertDatesResponseDTO getAvailableConcertDates(
            @PathVariable("concert_id") Long concertId,
            @RequestHeader("Authorization") String token) {

        List<ConcertDetail> concertDetails = Arrays.asList(
                new ConcertDetail(1L, "2024-10-01"),
                new ConcertDetail(2L, "2024-10-02")
        );

        return new ConcertDatesResponseDTO(concertId, "콘서트1", concertDetails);
    }
}
