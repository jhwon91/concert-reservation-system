package com.hhplus.concert.queue.domain;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueueTokenTest {

    private QueueToken queueToken;

    @BeforeEach
    void setUp() {
        queueToken = new QueueToken(UUID.randomUUID(), TokenValue.generate(), 1, 10);
    }

    @Test
    @DisplayName("토큰 발급 시 WAITING 상태로 생성된다")
    void create_status_waiting() {
        // given & when: setUp()에서 생성

        // then
        assertThat(queueToken.getStatus()).isEqualTo(QueueTokenStatus.WAITING);
    }

    @Test
    @DisplayName("WAITING → ACTIVE 활성화 성공")
    void activate_success() {
        // given: WAITING 상태 (setUp)

        // when
        queueToken.activate();

        // then
        assertThat(queueToken.getStatus()).isEqualTo(QueueTokenStatus.ACTIVE);
        assertThat(queueToken.getActivatedAt()).isNotNull();
        assertThat(queueToken.getExpiresAt()).isNotNull();
        assertThat(queueToken.getPosition()).isZero();
        assertThat(queueToken.getEstimatedWaitSec()).isZero();
    }

    @Test
    @DisplayName("ACTIVE 상태에서 activate 시 예외가 발생한다")
    void activate_already_active_throws() {
        // given
        queueToken.activate();

        // when & then
        assertThatThrownBy(() -> queueToken.activate())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.QUEUE_TOKEN_INVALID_STATUS);
    }

    @Test
    @DisplayName("ACTIVE → EXPIRED 만료 성공")
    void expire_from_active_success() {
        // given
        queueToken.activate();

        // when
        queueToken.expire();

        // then
        assertThat(queueToken.getStatus()).isEqualTo(QueueTokenStatus.EXPIRED);
    }

    @Test
    @DisplayName("WAITING → EXPIRED 직접 만료 가능하다")
    void expire_from_waiting_success() {
        // given: WAITING 상태 (setUp)

        // when
        queueToken.expire();

        // then
        assertThat(queueToken.getStatus()).isEqualTo(QueueTokenStatus.EXPIRED);
    }

    @Test
    @DisplayName("이미 EXPIRED 상태에서 expire 시 예외가 발생한다")
    void expire_already_expired_throws() {
        // given
        queueToken.expire();

        // when & then
        assertThatThrownBy(() -> queueToken.expire())
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.QUEUE_TOKEN_INVALID_STATUS);
    }

    @Test
    @DisplayName("ACTIVE 상태이고 만료 전이면 isActive()가 true다")
    void isActive_true() {
        // given
        queueToken.activate();

        // when & then
        assertThat(queueToken.isActive()).isTrue();
    }

    @Test
    @DisplayName("WAITING 상태이면 isActive()가 false다")
    void isActive_false_when_waiting() {
        // given: WAITING 상태 (setUp)

        // when & then
        assertThat(queueToken.isActive()).isFalse();
    }

    @Test
    @DisplayName("EXPIRED 상태이면 isExpired()가 true다")
    void isExpired_true() {
        // given
        queueToken.expire();

        // when & then
        assertThat(queueToken.isExpired()).isTrue();
    }

    @Test
    @DisplayName("순번과 예상대기시간이 업데이트된다")
    void updatePosition_success() {
        // given
        int newPosition = 5;
        int newWaitSec = 50;

        // when
        queueToken.updatePosition(newPosition, newWaitSec);

        // then
        assertThat(queueToken.getPosition()).isEqualTo(newPosition);
        assertThat(queueToken.getEstimatedWaitSec()).isEqualTo(newWaitSec);
    }
}
