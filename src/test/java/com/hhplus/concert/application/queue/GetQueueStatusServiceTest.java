package com.hhplus.concert.application.queue;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.application.dto.GetQueueStatusQuery;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.application.service.GetQueueStatusService;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetQueueStatusServiceTest {

    @InjectMocks
    private GetQueueStatusService getQueueStatusService;

    @Mock
    private QueueTokenRepository queueTokenRepository;

    @Test
    @DisplayName("토큰 값으로 대기 상태를 조회할 수 있다")
    void getStatus_success() {
        // given
        UUID userId = UUID.randomUUID();
        QueueToken token = new QueueToken(userId, TokenValue.generate(), 5, 50);
        given(queueTokenRepository.findByTokenValue(token.getTokenValue())).willReturn(Optional.of(token));

        // when
        QueueTokenResponse response = getQueueStatusService.getStatus(new GetQueueStatusQuery(token.getTokenValue()));

        // then
        assertThat(response.status()).isEqualTo(QueueTokenStatus.WAITING);
        assertThat(response.position()).isEqualTo(5);
        assertThat(response.estimatedWaitSec()).isEqualTo(50);
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 예외가 발생한다")
    void getStatus_token_not_found_throws() {
        // given
        String tokenValue = "not-exist-token";
        given(queueTokenRepository.findByTokenValue(tokenValue)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getQueueStatusService.getStatus(new GetQueueStatusQuery(tokenValue)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.TOKEN_NOT_FOUND);
    }
}
