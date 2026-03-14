package com.hhplus.concert.application.queue;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.application.dto.IssueQueueTokenCommand;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.application.service.IssueQueueTokenService;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.service.QueueDomainService;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import com.hhplus.concert.user.domain.User;
import com.hhplus.concert.user.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IssueQueueTokenServiceTest {

    @InjectMocks
    private IssueQueueTokenService issueQueueTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueTokenRepository queueTokenRepository;

    @Mock
    private QueueDomainService queueDomainService;

    @Test
    @DisplayName("유효한 유저에게 WAITING 토큰이 발급된다")
    void issue_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("테스터", "test@test.com", "010-0000-0000");
        QueueToken token = new QueueToken(userId, TokenValue.generate(), 1, 10);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(queueTokenRepository.countByStatus(QueueTokenStatus.WAITING)).willReturn(0L);
        given(queueDomainService.issueToken(userId, 0)).willReturn(token);
        given(queueTokenRepository.save(any())).willReturn(token);

        // when
        QueueTokenResponse response = issueQueueTokenService.issue(new IssueQueueTokenCommand(userId));

        // then
        assertThat(response.status()).isEqualTo(QueueTokenStatus.WAITING);
        assertThat(response.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 토큰 발급 시 예외가 발생한다")
    void issue_user_not_found_throws() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueQueueTokenService.issue(new IssueQueueTokenCommand(userId)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.USER_NOT_FOUND);
    }
}
