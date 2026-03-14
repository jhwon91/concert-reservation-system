package com.hhplus.concert.user.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.domain.service.PointDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointDomainServiceTest {

    private PointDomainService pointDomainService;
    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        pointDomainService = new PointDomainService();
        userPoint = new UserPoint(UUID.randomUUID());
    }

    @Test
    @DisplayName("충전 후 PointTransaction이 생성된다")
    void charge_returns_transaction() {
        PointTransaction tx = pointDomainService.charge(userPoint, BigDecimal.valueOf(10_000));

        assertThat(tx).isNotNull();
        assertThat(tx.getType()).isEqualTo(PointTransactionType.CHARGE);
        assertThat(tx.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(tx.getBalanceAfter()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
    }

    @Test
    @DisplayName("충전 후 UserPoint 잔액이 증가한다")
    void charge_increases_balance() {
        pointDomainService.charge(userPoint, BigDecimal.valueOf(10_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
    }

    @Test
    @DisplayName("최대 보유 잔액 초과 시 예외가 발생한다")
    void charge_exceeds_max_balance_throws() {
        pointDomainService.charge(userPoint, BigDecimal.valueOf(9_000_000));

        assertThatThrownBy(() -> pointDomainService.charge(userPoint, BigDecimal.valueOf(2_000_000)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.MAX_BALANCE_EXCEEDED);
    }

    @Test
    @DisplayName("정확히 최대 잔액까지 충전 가능하다")
    void charge_up_to_max_balance_success() {
        PointTransaction tx = pointDomainService.charge(userPoint, BigDecimal.valueOf(10_000_000));

        assertThat(userPoint.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10_000_000));
        assertThat(tx.getBalanceAfter()).isEqualByComparingTo(BigDecimal.valueOf(10_000_000));
    }

    @Test
    @DisplayName("사용 후 PointTransaction이 생성된다")
    void use_returns_transaction() {
        pointDomainService.charge(userPoint, BigDecimal.valueOf(10_000));
        UUID relatedId = UUID.randomUUID();

        PointTransaction tx = pointDomainService.use(userPoint, BigDecimal.valueOf(3_000), relatedId);

        assertThat(tx.getType()).isEqualTo(PointTransactionType.USE);
        assertThat(tx.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(3_000));
        assertThat(tx.getBalanceAfter()).isEqualByComparingTo(BigDecimal.valueOf(7_000));
        assertThat(tx.getRelatedId()).isEqualTo(relatedId);
    }
}
