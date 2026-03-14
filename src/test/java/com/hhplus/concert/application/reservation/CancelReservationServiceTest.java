package com.hhplus.concert.application.reservation;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import com.hhplus.concert.reservation.application.dto.CancelReservationCommand;
import com.hhplus.concert.reservation.application.service.CancelReservationService;
import com.hhplus.concert.reservation.domain.Reservation;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelReservationServiceTest {

    @InjectMocks
    private CancelReservationService cancelReservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Test
    @DisplayName("본인의 예약을 취소할 수 있다")
    void cancel_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        Seat seat = new Seat(concertDateId, SeatNumber.of(5), BigDecimal.valueOf(100_000));
        seat.reserve();
        Reservation reservation = new Reservation(userId, seat.getSeatId(), concertDateId, "token");

        given(reservationRepository.findById(reservation.getReservationId())).willReturn(Optional.of(reservation));
        given(seatRepository.findById(seat.getSeatId())).willReturn(Optional.of(seat));
        given(reservationRepository.save(any())).willReturn(reservation);
        given(seatRepository.save(any())).willReturn(seat);

        // when
        cancelReservationService.cancel(new CancelReservationCommand(reservation.getReservationId(), userId));

        // then
        verify(reservationRepository).save(reservation);
        verify(seatRepository).save(seat);
    }

    @Test
    @DisplayName("존재하지 않는 예약 취소 시 예외가 발생한다")
    void cancel_reservation_not_found_throws() {
        // given
        UUID reservationId = UUID.randomUUID();
        given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cancelReservationService.cancel(
                new CancelReservationCommand(reservationId, UUID.randomUUID())))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 유저의 예약 취소 시 예외가 발생한다")
    void cancel_other_user_reservation_throws() {
        // given
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        Seat seat = new Seat(concertDateId, SeatNumber.of(5), BigDecimal.valueOf(100_000));
        Reservation reservation = new Reservation(ownerId, seat.getSeatId(), concertDateId, "token");

        given(reservationRepository.findById(reservation.getReservationId())).willReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> cancelReservationService.cancel(
                new CancelReservationCommand(reservation.getReservationId(), otherUserId)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.RESERVATION_ACCESS_DENIED);
    }
}
