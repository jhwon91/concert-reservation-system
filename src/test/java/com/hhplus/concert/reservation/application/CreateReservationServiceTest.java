package com.hhplus.concert.reservation.application;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.concert.domain.service.ConcertDomainService;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import com.hhplus.concert.reservation.application.dto.CreateReservationCommand;
import com.hhplus.concert.reservation.application.dto.ReservationResponse;
import com.hhplus.concert.reservation.application.service.CreateReservationService;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
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
class CreateReservationServiceTest {

    @InjectMocks
    private CreateReservationService createReservationService;

    @Mock
    private QueueTokenRepository queueTokenRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConcertDomainService concertDomainService;

    @Test
    @DisplayName("활성 토큰으로 좌석 예약에 성공한다")
    void create_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        String tokenValue = "active-token";
        QueueToken token = new QueueToken(userId, TokenValue.of(tokenValue), 1, 10);
        token.activate();

        Seat seat = new Seat(concertDateId, SeatNumber.of(10), BigDecimal.valueOf(100_000));
        Reservation reservation = new Reservation(userId, seat.getSeatId(), concertDateId, tokenValue);

        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.of(token));
        given(seatRepository.findByIdWithLock(seat.getSeatId())).willReturn(Optional.of(seat));
        given(seatRepository.save(any())).willReturn(seat);
        given(reservationRepository.save(any())).willReturn(reservation);

        // when
        ReservationResponse response = createReservationService.create(
                new CreateReservationCommand(userId, seat.getSeatId(), tokenValue));

        // then
        assertThat(response.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("존재하지 않는 토큰으로 예약 시 예외가 발생한다")
    void create_token_not_found_throws() {
        // given
        String tokenValue = "not-exist-token";
        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createReservationService.create(
                new CreateReservationCommand(UUID.randomUUID(), UUID.randomUUID(), tokenValue)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("비활성 토큰으로 예약 시 예외가 발생한다")
    void create_inactive_token_throws() {
        // given
        UUID userId = UUID.randomUUID();
        String tokenValue = "waiting-token";
        QueueToken waitingToken = new QueueToken(userId, TokenValue.of(tokenValue), 5, 50);

        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.of(waitingToken));

        // when & then
        assertThatThrownBy(() -> createReservationService.create(
                new CreateReservationCommand(userId, UUID.randomUUID(), tokenValue)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.QUEUE_TOKEN_INVALID_STATUS);
    }

    @Test
    @DisplayName("존재하지 않는 좌석 예약 시 예외가 발생한다")
    void create_seat_not_found_throws() {
        // given
        UUID userId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        String tokenValue = "active-token";
        QueueToken token = new QueueToken(userId, TokenValue.of(tokenValue), 1, 10);
        token.activate();

        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.of(token));
        given(seatRepository.findByIdWithLock(seatId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createReservationService.create(
                new CreateReservationCommand(userId, seatId, tokenValue)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.SEAT_NOT_FOUND);
    }
}
