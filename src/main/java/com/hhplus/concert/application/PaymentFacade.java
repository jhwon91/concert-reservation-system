package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.PaymentResult;
import com.hhplus.concert.domain.entity.*;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentFacade {
    private final QueueService queueService;
    private final UserService userService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final ConcertDetailService concertDetailService;
    private final PaymentService paymentService;

    @Autowired
    public PaymentFacade(QueueService queueService, UserService userService, SeatService seatService, ReservationService reservationService, ConcertDetailService concertDetailService, PaymentService paymentService) {
        this.queueService = queueService;
        this.userService = userService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.concertDetailService = concertDetailService;
        this.paymentService = paymentService;
    }

    /**
     * 1. 토큰 건증
     * 2. token userid 와 request userid 검증
     * 3. 임시 배정 좌석 확인(예약시간기준으로 5분 지났는지 확인)
     * 4. 사용자 잔액 확인
     * 5. 잔액 차감
     * 6. 결제 내역 생성
     * 7. 좌석 상태 변경
     * 8. 토큰 상태 변경
     * 9. 다음 대기자 활성 처리?
     */
    @Transactional
    public PaymentResult.Payment paymentConcert(UUID token, Long userId, Long reservationId){
        queueService.validationToken(token);

        Queue queue = queueService.getQueueByToken(token);
        User user = userService.findUserById(userId);
        queueService.validationUser(queue, user);

        Reservation reservation = reservationService.getReservation(reservationId);
        ConcertDetails concertDetails = concertDetailService.getConcertDetail(reservation.getConcertDetailId());
        Seat seat = seatService.getSeat(reservation.getSeatId());

        //임시 배정 좌석 확인(예약시간기준으로 5분 지났는지 확인)
        seatService.checkTemporaryStatus(seat);
        reservationService.checkReservationStatus(reservation, LocalDateTime.now());

        //사용자 잔액 확인
        userService.checkComparePoint(user, concertDetails);

        User changeUser = userService.usePoint(user, concertDetails);
        User paymentUser = userService.save(changeUser);

        Payment enterPayment = paymentService.enterReservation(paymentUser, reservation, concertDetails);
        paymentService.save(enterPayment);

        seatService.changeSeatStatus(seat,SeatStatus.RESERVED);

        Queue changeQueueStatus = queueService.changeQueueStatus(queue, TokenStatus.EXPIRED, Optional.of(LocalDateTime.now()));
        queueService.save(changeQueueStatus);

        return PaymentResult.Payment.builder()
                .user_id(paymentUser.getId())
                .user_name(paymentUser.getName())
                .point(paymentUser.getPoint())
                .status(seat.getStatus())
                .build();
    }
}
