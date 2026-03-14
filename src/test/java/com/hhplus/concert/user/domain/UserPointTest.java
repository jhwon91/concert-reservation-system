package com.hhplus.concert.user.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTest {

    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        userPoint = new UserPoint(UUID.randomUUID());
    }

    @Test
    @DisplayName("포인트 충전 시 잔액이 증가한다")
    void charge_success() {
        userPoint.charge(BigDecimal.valueOf(10_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
    }

    @Test
    @DisplayName("포인트 충전 시 잔액이 누적된다")
    void charge_accumulates() {
        userPoint.charge(BigDecimal.valueOf(10_000));
        userPoint.charge(BigDecimal.valueOf(5_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(15_000));
    }

    @Test
    @DisplayName("0 이하 금액 충전 시 예외가 발생한다")
    void charge_zero_throws() {
        assertThatThrownBy(() -> userPoint.charge(BigDecimal.ZERO))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MONEY_AMOUNT);
    }

    @Test
    @DisplayName("음수 금액 충전 시 예외가 발생한다")
    void charge_negative_throws() {
        assertThatThrownBy(() -> userPoint.charge(BigDecimal.valueOf(-1_000)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MONEY_AMOUNT);
    }

    @Test
    @DisplayName("포인트 사용 시 잔액이 차감된다")
    void use_success() {
        userPoint.charge(BigDecimal.valueOf(10_000));
        userPoint.use(BigDecimal.valueOf(3_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(7_000));
    }

    @Test
    @DisplayName("잔액 전액 사용이 가능하다")
    void use_all_balance() {
        userPoint.charge(BigDecimal.valueOf(10_000));
        userPoint.use(BigDecimal.valueOf(10_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("잔액보다 많은 금액 사용 시 예외가 발생한다")
    void use_insufficient_balance_throws() {
        userPoint.charge(BigDecimal.valueOf(5_000));

        assertThatThrownBy(() -> userPoint.use(BigDecimal.valueOf(10_000)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INSUFFICIENT_BALANCE);
    }

    @Test
    @DisplayName("0 이하 금액 사용 시 예외가 발생한다")
    void use_zero_throws() {
        assertThatThrownBy(() -> userPoint.use(BigDecimal.ZERO))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MONEY_AMOUNT);
    }
}
