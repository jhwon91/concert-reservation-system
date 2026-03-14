package com.hhplus.concert.reservation.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token-value"
        );
    }

    @Test
    @DisplayName("예약 생성 시 PENDING 상태다")
    void create_status_pending() {
        // given & when: setUp()에서 생성

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("예약 생성 시 만료 시간은 5분 후다")
    void create_expires_at_5_minutes() {
        // given & when: setUp()에서 생성

        // then
        assertThat(reservation.getExpiresAt())
                .isAfter(reservation.getReservedAt())
                .isBefore(reservation.getReservedAt().plusMinutes(6));
    }

    @Test
    @DisplayName("PENDING → CONFIRMED 확정 성공")
    void confirm_success() {
        // given: PENDING 상태 (setUp)

        // when
        reservation.confirm();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getConfirmedAt()).isNotNull();
    }

    @Test
    @DisplayName("PENDING이 아닌 예약 확정 시 예외가 발생한다")
    void confirm_not_pending_throws() {
        // given
        reservation.confirm();

        // when & then
        assertThatThrownBy(() -> reservation.confirm())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.RESERVATION_NOT_PENDING);
    }

    @Test
    @DisplayName("PENDING → CANCELLED 취소 성공")
    void cancel_success() {
        // given: PENDING 상태 (setUp)

        // when
        reservation.cancel();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("CONFIRMED 예약도 취소 가능하다")
    void cancel_confirmed_success() {
        // given
        reservation.confirm();

        // when
        reservation.cancel();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("이미 취소된 예약 재취소 시 예외가 발생한다")
    void cancel_already_cancelled_throws() {
        // given
        reservation.cancel();

        // when & then
        assertThatThrownBy(() -> reservation.cancel())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.RESERVATION_ALREADY_CANCELLED);
    }

    @Test
    @DisplayName("만료 전이면 isExpired()가 false다")
    void isExpired_false_before_expiry() {
        // given: 방금 생성된 예약 (setUp)

        // when & then
        assertThat(reservation.isExpired()).isFalse();
    }
}
