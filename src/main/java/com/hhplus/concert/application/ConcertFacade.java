package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.ConcertCommand;
import com.hhplus.concert.application.dto.ConcertResult;
import com.hhplus.concert.domain.entity.*;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConcertFacade {
    private final ConcertDetailService concertDetailService;
    private final ConcertService concertService;
    private final QueueService queueService;
    private final SeatService seatService;
    private final UserService userService;
    private final ReservationService reservationService;

    @Autowired
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
    public ConcertResult.ConcertAvailableDates getAvailableConcertDates(ConcertCommand.availableConcertDates command) {
        queueService.validationToken(command.token());
        Concert concert = concertService.getConcert(command.concertId());
        List<ConcertDetails> concertDetailsList = concertDetailService.getAvailableConcertDates(concert.getId());

        return ConcertResult.ConcertAvailableDates.from(concert, concertDetailsList);
    }

    /**
     *  특정 날짜의 예약 가능한 좌석 조회
     */
    public ConcertResult.ConcertAvailableSeats getAvailableSeats(ConcertCommand.availableConcertSeats command) {
        queueService.validationToken(command.token());
        Concert concert = concertService.getConcert(command.concertId());
        ConcertDetails concertDetails = concertDetailService.getConcertDetail(command.concertDetailId());
        List<Seat> seats = seatService.getAvailableSeat(concertDetails.getId(), SeatStatus.AVAILABLE);

        return ConcertResult.ConcertAvailableSeats.from(concert, concertDetails, seats);
    }

    /**
     *  좌석 예약
     */
    @Transactional
    public ConcertResult.ConcertReservation reserveSeat(ConcertCommand.reserveSeat command){
        queueService.validationToken(command.token());
        User user = userService.findUserById(command.userId());

        ConcertDetails concertDetails = concertDetailService.getConcertDetail(command.concertDetailId());
        Concert concert = concertService.getConcert(concertDetails.getConcertId());

        Seat seat = seatService.getSeat(command.seatId());
        seatService.checkAvailableStatus(seat);

        Reservation reservation = reservationService.enterReservation(user,concertDetails,seat);
        Reservation saveReservation = reservationService.save(reservation);

        Seat changeSeat = seatService.changeSeatStatus(seat,SeatStatus.TEMPORARY_ALLOCATED);
        Seat updateSeat = seatService.save(changeSeat);
        return ConcertResult.ConcertReservation.from(saveReservation, user,concertDetails, concert, updateSeat);
    }
}
