package com.hhplus.concert.application.user;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.application.dto.GetUserPointQuery;
import com.hhplus.concert.user.application.dto.UserPointResponse;
import com.hhplus.concert.user.application.service.GetUserPointService;
import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetUserPointServiceTest {

    @InjectMocks
    private GetUserPointService getUserPointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Test
    @DisplayName("유저 포인트 잔액을 조회할 수 있다")
    void getPoint_success() {
        // given
        UUID userId = UUID.randomUUID();
        UserPoint userPoint = new UserPoint(userId);
        userPoint.charge(BigDecimal.valueOf(50_000));

        given(userPointRepository.findByUserId(userId)).willReturn(Optional.of(userPoint));

        // when
        UserPointResponse response = getUserPointService.getPoint(new GetUserPointQuery(userId));

        // then
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.valueOf(50_000));
    }

    @Test
    @DisplayName("포인트가 없는 유저 조회 시 예외가 발생한다")
    void getPoint_not_found_throws() {
        // given
        UUID userId = UUID.randomUUID();
        given(userPointRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getUserPointService.getPoint(new GetUserPointQuery(userId)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.USER_POINT_NOT_FOUND);
    }
}
