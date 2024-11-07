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
    public ConcertResult.ConcertAvailableDates getAvailableConcertDates(ConcertCommand.AvailableConcertDates command) {
        Queue queue = queueService.getQueueByToken(command.token());
        queueService.validateActiveToken(queue);

        Concert concert = concertService.getConcert(command.concertId());
        List<ConcertDetails> concertDetailsList = concertDetailService.getAvailableConcertDates(concert.getId());

        return ConcertResult.ConcertAvailableDates.from(concert, concertDetailsList);
    }

    /**
     *  특정 날짜의 예약 가능한 좌석 조회
     */
    public ConcertResult.ConcertAvailableSeats getAvailableSeats(ConcertCommand.AvailableConcertSeats command) {
        Queue queue = queueService.getQueueByToken(command.token());
        queueService.validateActiveToken(queue);

        Concert concert = concertService.getConcert(command.concertId());
        ConcertDetails concertDetails = concertDetailService.getConcertDetail(command.concertDetailId());
        List<Seat> seats = seatService.getAvailableSeat(concertDetails.getId(), SeatStatus.AVAILABLE);

        return ConcertResult.ConcertAvailableSeats.from(concert, concertDetails, seats);
    }

    /**
     *  좌석 예약
     */
    @Transactional
    public ConcertResult.ConcertReservation reserveSeat(ConcertCommand.ReserveSeat command){
        // 1. 토큰 검증
        Queue queue = queueService.getQueueByToken(command.token());
        queueService.validateActiveToken(queue);

        // 2. 콘서트와 좌석 정보 가져오기
        User user = userService.findUserById(command.userId());
        ConcertDetails concertDetails = concertDetailService.getConcertDetail(command.concertDetailId());
        Concert concert = concertService.getConcert(concertDetails.getConcertId());
        Seat seat = seatService.findAvailableSeat(command.seatId());

        // 3. 예약 생성 및 저장
        Reservation reservation = reservationService.createAndSaveReservation(user,concertDetails,seat);

        // 4. 좌석 상태 변경
        seatService.changeSeatStatus(seat,SeatStatus.TEMPORARY_ALLOCATED);

        return ConcertResult.ConcertReservation.from(reservation, user,concertDetails, concert, seat);
    }
}
