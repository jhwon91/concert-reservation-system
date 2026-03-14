package com.hhplus.concert.payment.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.payment.application.dto.PaymentResponse;
import com.hhplus.concert.payment.application.dto.ProcessPaymentCommand;
import com.hhplus.concert.payment.domain.Payment;
import com.hhplus.concert.payment.domain.repository.PaymentRepository;
import com.hhplus.concert.payment.domain.service.PaymentDomainService;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import com.hhplus.concert.reservation.domain.service.ReservationDomainService;
import com.hhplus.concert.user.domain.PointTransaction;
import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.domain.repository.PointTransactionRepository;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
import com.hhplus.concert.user.domain.service.PointDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessPaymentService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserPointRepository userPointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final QueueTokenRepository queueTokenRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationDomainService reservationDomainService;
    private final PointDomainService pointDomainService;
    private final PaymentDomainService paymentDomainService;

    @Transactional
    public PaymentResponse process(ProcessPaymentCommand command) {
        // 1. 예약 조회 및 검증
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new CoreException(ErrorType.RESERVATION_NOT_FOUND, command.reservationId()));
        reservationDomainService.validatePending(reservation);

        // 2. 좌석 조회
        Seat seat = seatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new CoreException(ErrorType.SEAT_NOT_FOUND, reservation.getSeatId()));

        // 3. 포인트 차감 (비관적 잠금)
        UserPoint userPoint = userPointRepository.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.USER_POINT_NOT_FOUND, command.userId()));

        // 4. 결제 생성
        Payment payment = paymentDomainService.createPayment(
                reservation.getReservationId(),
                command.userId(),
                seat.getPrice(),
                command.paymentMethod()
        );

        // 5. 포인트 차감 및 거래 내역 생성
        PointTransaction transaction = pointDomainService.use(userPoint, seat.getPrice(), payment.getPaymentId());

        // 6. 상태 전이
        payment.complete();
        reservation.confirm();
        seat.occupy();

        // 7. 토큰 만료
        QueueToken token = queueTokenRepository.findByTokenValue(reservation.getTokenValue())
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, reservation.getTokenValue()));
        token.expire();

        // 8. 저장
        userPointRepository.save(userPoint);
        pointTransactionRepository.save(transaction);
        paymentRepository.save(payment);
        reservationRepository.save(reservation);
        seatRepository.save(seat);
        queueTokenRepository.save(token);

        return PaymentResponse.from(payment);
    }
}
