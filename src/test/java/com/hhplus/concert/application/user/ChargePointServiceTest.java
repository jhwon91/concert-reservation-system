package com.hhplus.concert.application.user;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.application.dto.ChargePointCommand;
import com.hhplus.concert.user.application.dto.UserPointResponse;
import com.hhplus.concert.user.application.service.ChargePointService;
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
class ChargePointServiceTest {

    @InjectMocks
    private ChargePointService chargePointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @Mock
    private PointDomainService pointDomainService;

    @Test
    @DisplayName("포인트 충전 후 잔액이 반환된다")
    void charge_success() {
        // given
        UUID userId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(10_000);
        UserPoint userPoint = new UserPoint(userId);
        PointTransaction transaction = new PointTransaction(userId, amount, amount, PointTransactionType.CHARGE);

        given(userPointRepository.findByUserIdWithLock(userId)).willReturn(Optional.of(userPoint));
        given(pointDomainService.charge(userPoint, amount)).willReturn(transaction);
        given(userPointRepository.save(any())).willReturn(userPoint);
        given(pointTransactionRepository.save(any())).willReturn(transaction);

        // when
        UserPointResponse response = chargePointService.charge(new ChargePointCommand(userId, amount));

        // then
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("포인트가 없는 유저 충전 시 예외가 발생한다")
    void charge_user_point_not_found_throws() {
        // given
        UUID userId = UUID.randomUUID();
        given(userPointRepository.findByUserIdWithLock(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chargePointService.charge(new ChargePointCommand(userId, BigDecimal.valueOf(10_000))))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.USER_POINT_NOT_FOUND);
    }
}
