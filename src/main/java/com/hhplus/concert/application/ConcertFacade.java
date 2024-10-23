package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.ConcertResult;
import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.service.ConcertDetailService;
import com.hhplus.concert.domain.service.ConcertService;
import com.hhplus.concert.domain.service.QueueService;
import com.hhplus.concert.domain.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertFacade {
    private final ConcertDetailService concertDetailService;
    private final ConcertService concertService;
    private final QueueService queueService;
    private final SeatService seatService;

    public ConcertFacade(ConcertDetailService concertDetailService, ConcertService concertService, QueueService queueService, SeatService seatService) {
        this.concertDetailService = concertDetailService;
        this.concertService = concertService;
        this.queueService = queueService;
        this.seatService = seatService;
    }

    /**
     *  예약 가능한 날짜 조회
     */
    public ConcertResult.ConcertAvailableDates getAvailableConcertDates(Long concertId, String token) {
        queueService.validationToken(token);
        Concert concert = concertService.getConcert(concertId);

        List<ConcertResult.ConcertDetails> concertDetailsList = concertDetailService.getAvailableConcertDates(concert.getId()).stream()
                .map(entity -> ConcertResult.ConcertDetails.builder()
                        .concert_detail_id(entity.getId())
                        .concert_id(entity.getConcert_id())
                        .max_seat(entity.getMax_seat())
                        .price(entity.getPrice())
                        .concert_date(entity.getConcert_date())
                        .build())
                .collect(Collectors.toList());

        return ConcertResult.ConcertAvailableDates.builder()
                .concert_id(concert.getId())
                .concert_title(concert.getTitle())
                .concert_detail(concertDetailsList)
                .build();
    }

    /**
     * 4. 특정 날짜의 예약 가능한 좌석 조회
     */
    public ConcertResult.ConcertAvailableSeats getAvailableSeats(String token, Long concertId, Long concertDetailId) {
        queueService.validationToken(token);
        Concert concert = concertService.getConcert(concertId);
        ConcertDetails concertDetails = concertDetailService.getConcertDetail(concertDetailId);

        List<ConcertResult.Seat> seats = seatService.getAvailableSeat(concertDetails.getId(), SeatStatus.AVAILABLE).stream()
                .map(entity -> ConcertResult.Seat.builder()
                        .seat_id(entity.getId())
                        .seat_number(entity.getSeat_number())
                        .status(entity.getStatus())
                        .build())
                .collect(Collectors.toList());


        return ConcertResult.ConcertAvailableSeats.builder()
                .concert_id(concert.getId())
                .concert_title(concert.getTitle())
                .concert_detail_id(concertDetails.getId())
                .concert_date(concertDetails.getConcert_date())
                .max_seat(concertDetails.getMax_seat())
                .seats(seats)
                .build();
    }

    /**
     * 5. 좌석 예약
     */
    public ConcertResult.ConcertReservation reserveSeat(){

        return null;
    }
}
