package com.hhplus.concert.payment.application;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import com.hhplus.concert.payment.application.dto.PaymentResponse;
import com.hhplus.concert.payment.application.dto.ProcessPaymentCommand;
import com.hhplus.concert.payment.application.service.ProcessPaymentService;
import com.hhplus.concert.payment.domain.Payment;
import com.hhplus.concert.payment.domain.PaymentMethod;
import com.hhplus.concert.payment.domain.PaymentStatus;
import com.hhplus.concert.payment.domain.repository.PaymentRepository;
import com.hhplus.concert.payment.domain.service.PaymentDomainService;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import com.hhplus.concert.reservation.domain.service.ReservationDomainService;
import com.hhplus.concert.user.domain.PointTransaction;
import com.hhplus.concert.user.domain.PointTransactionType;
import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.domain.repository.PointTransactionRepository;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
import com.hhplus.concert.user.domain.service.PointDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentServiceTest {

    @InjectMocks
    private ProcessPaymentService processPaymentService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @Mock
    private QueueTokenRepository queueTokenRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationDomainService reservationDomainService;

    @Mock
    private PointDomainService pointDomainService;

    @Mock
    private PaymentDomainService paymentDomainService;

    @Test
    @DisplayName("결제에 성공하면 COMPLETED 상태의 결제 응답을 반환한다")
    void process_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(100_000);
        String tokenValue = "active-token";

        Seat seat = new Seat(concertDateId, SeatNumber.of(1), price);
        seat.reserve();
        Reservation reservation = new Reservation(userId, seat.getSeatId(), concertDateId, tokenValue);
        UserPoint userPoint = new UserPoint(userId);
        userPoint.charge(BigDecimal.valueOf(200_000));
        Payment payment = new Payment(reservation.getReservationId(), userId, price, PaymentMethod.POINT);
        PointTransaction transaction = new PointTransaction(userId, price, BigDecimal.valueOf(100_000), PointTransactionType.USE, payment.getPaymentId());
        QueueToken token = new QueueToken(userId, TokenValue.of(tokenValue), 1, 10);
        token.activate();

        given(reservationRepository.findById(reservation.getReservationId())).willReturn(Optional.of(reservation));
        given(seatRepository.findById(seat.getSeatId())).willReturn(Optional.of(seat));
        given(userPointRepository.findByUserIdWithLock(userId)).willReturn(Optional.of(userPoint));
        given(paymentDomainService.createPayment(any(), any(), any(), any())).willReturn(payment);
        given(pointDomainService.use(any(), any(), any())).willReturn(transaction);
        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.of(token));
        given(paymentRepository.save(any())).willReturn(payment);

        // when
        PaymentResponse response = processPaymentService.process(
                new ProcessPaymentCommand(userId, reservation.getReservationId(), PaymentMethod.POINT));

        // then
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("존재하지 않는 예약으로 결제 시 예외가 발생한다")
    void process_reservation_not_found_throws() {
        // given
        UUID reservationId = UUID.randomUUID();
        given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> processPaymentService.process(
                new ProcessPaymentCommand(UUID.randomUUID(), reservationId, PaymentMethod.POINT)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("포인트가 없는 유저 결제 시 예외가 발생한다")
    void process_user_point_not_found_throws() {
        // given
        UUID userId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        String tokenValue = "token";
        Seat seat = new Seat(concertDateId, SeatNumber.of(1), BigDecimal.valueOf(100_000));
        Reservation reservation = new Reservation(userId, seat.getSeatId(), concertDateId, tokenValue);

        given(reservationRepository.findById(reservation.getReservationId())).willReturn(Optional.of(reservation));
        given(seatRepository.findById(seat.getSeatId())).willReturn(Optional.of(seat));
        given(userPointRepository.findByUserIdWithLock(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> processPaymentService.process(
                new ProcessPaymentCommand(userId, reservation.getReservationId(), PaymentMethod.POINT)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.USER_POINT_NOT_FOUND);
    }
}
