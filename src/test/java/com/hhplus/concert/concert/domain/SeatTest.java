package com.hhplus.concert.concert.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat(UUID.randomUUID(), SeatNumber.of(10), BigDecimal.valueOf(150_000));
    }

    @Test
    @DisplayName("좌석 생성 시 AVAILABLE 상태다")
    void create_status_available() {
        // given & when: setUp()에서 생성

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("AVAILABLE → RESERVED 예약 성공")
    void reserve_success() {
        // given: AVAILABLE 상태 (setUp)

        // when
        seat.reserve();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @Test
    @DisplayName("AVAILABLE이 아닌 좌석 예약 시 예외가 발생한다")
    void reserve_not_available_throws() {
        // given
        seat.reserve();

        // when & then
        assertThatThrownBy(() -> seat.reserve())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.SEAT_NOT_AVAILABLE);
    }

    @Test
    @DisplayName("RESERVED → OCCUPIED 점유 성공")
    void occupy_success() {
        // given
        seat.reserve();

        // when
        seat.occupy();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.OCCUPIED);
    }

    @Test
    @DisplayName("RESERVED가 아닌 좌석 점유 시 예외가 발생한다")
    void occupy_not_reserved_throws() {
        // given: AVAILABLE 상태 (setUp)

        // when & then
        assertThatThrownBy(() -> seat.occupy())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.SEAT_NOT_RESERVED);
    }

    @Test
    @DisplayName("RESERVED → AVAILABLE 해제 성공")
    void release_success() {
        // given
        seat.reserve();

        // when
        seat.release();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("OCCUPIED 좌석 해제 시 예외가 발생한다")
    void release_occupied_throws() {
        // given
        seat.reserve();
        seat.occupy();

        // when & then
        assertThatThrownBy(() -> seat.release())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.SEAT_NOT_RESERVED);
    }
}
