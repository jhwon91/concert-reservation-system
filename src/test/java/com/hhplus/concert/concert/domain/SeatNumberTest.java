package com.hhplus.concert.concert.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatNumberTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 25, 50})
    @DisplayName("1~50 사이의 좌석 번호는 정상 생성된다")
    void of_valid(int value) {
        // given & when
        SeatNumber seatNumber = SeatNumber.of(value);

        // then
        assertThat(seatNumber.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("경계값 1은 정상 생성된다")
    void of_min_boundary() {
        // given & when
        SeatNumber seatNumber = SeatNumber.of(1);

        // then
        assertThat(seatNumber.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("경계값 50은 정상 생성된다")
    void of_max_boundary() {
        // given & when
        SeatNumber seatNumber = SeatNumber.of(50);

        // then
        assertThat(seatNumber.getValue()).isEqualTo(50);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 51, 100})
    @DisplayName("1~50 범위를 벗어나면 예외가 발생한다")
    void of_invalid_throws(int value) {
        // given & when & then
        assertThatThrownBy(() -> SeatNumber.of(value))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_SEAT_NUMBER);
    }

    @Test
    @DisplayName("같은 값이면 동등하다")
    void equals_same_value() {
        // given
        SeatNumber a = SeatNumber.of(10);
        SeatNumber b = SeatNumber.of(10);

        // when & then
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("다른 값이면 동등하지 않다")
    void equals_different_value() {
        // given
        SeatNumber a = SeatNumber.of(10);
        SeatNumber b = SeatNumber.of(20);

        // when & then
        assertThat(a).isNotEqualTo(b);
    }
}
