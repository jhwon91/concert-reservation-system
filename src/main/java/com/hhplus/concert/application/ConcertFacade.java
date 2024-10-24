package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.ConcertResult;
import com.hhplus.concert.domain.entity.*;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertFacade {
    private final ConcertDetailService concertDetailService;
    private final ConcertService concertService;
    private final QueueService queueService;
    private final SeatService seatService;
    private final UserService userService;
    private final ReservationService reservationService;

    public ConcertFacade(ConcertDetailService concertDetailService, ConcertService concertService, QueueService queueService, SeatService seatService, UserService userService, ReservationService reservationService) {
        this.concertDetailService = concertDetailService;
        this.concertService = concertService;
        this.queueService = queueService;
        this.seatService = seatService;
        this.userService = userService;
        this.reservationService = reservationService;
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
     *  특정 날짜의 예약 가능한 좌석 조회
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
     *  좌석 예약
     */
    @Transactional
    public ConcertResult.ConcertReservation reserveSeat(String token, Long userId, Long concertDetailId,  Long seatId){
        queueService.validationToken(token);
        User user = userService.findUserById(userId);

        ConcertDetails concertDetails = concertDetailService.getConcertDetail(concertDetailId);
        Concert concert = concertService.getConcert(concertDetails.getConcert_id());

        Seat seat = seatService.getSeat(seatId);
        seatService.checkAvailableStatus(seat);

        Reservation reservation = reservationService.enterReservation(user,concertDetails,seat);
        Reservation saveReservation = reservationService.save(reservation);

        Seat changeSeat = seatService.changeSeatStatus(seat,SeatStatus.TEMPORARY_ALLOCATED);
        Seat updateSeat = seatService.save(changeSeat);

        return ConcertResult.ConcertReservation.builder()
                .reservation_id(saveReservation.getId())
                .user_id(saveReservation.getUser_id())
                .user_name(user.getName())
                .concert_id(concertDetails.getConcert_id())
                .concert_name(concert.getTitle())
                .concert_detail_id(concertDetails.getId())
                .concert_date(concertDetails.getConcert_date())
                .seat_id(updateSeat.getId())
                .seat_number(updateSeat.getSeat_number())
                .status(updateSeat.getStatus())
                .reservation_at(saveReservation.getReservation_at())
                .build();
    }
}
