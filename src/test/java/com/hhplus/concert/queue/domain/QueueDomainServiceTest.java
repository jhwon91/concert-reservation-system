package com.hhplus.concert.queue.domain;

import com.hhplus.concert.queue.domain.service.QueueDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QueueDomainServiceTest {

    private QueueDomainService queueDomainService;

    @BeforeEach
    void setUp() {
        queueDomainService = new QueueDomainService();
    }

    @Test
    @DisplayName("토큰 발급 시 순번은 현재 대기 수 + 1이다")
    void issueToken_position() {
        // given
        int currentWaitingCount = 10;

        // when
        QueueToken token = queueDomainService.issueToken(UUID.randomUUID(), currentWaitingCount);

        // then
        assertThat(token.getPosition()).isEqualTo(11);
    }

    @Test
    @DisplayName("토큰 발급 시 WAITING 상태로 생성된다")
    void issueToken_status_waiting() {
        // given
        int currentWaitingCount = 0;

        // when
        QueueToken token = queueDomainService.issueToken(UUID.randomUUID(), currentWaitingCount);

        // then
        assertThat(token.getStatus()).isEqualTo(QueueTokenStatus.WAITING);
    }

    @Test
    @DisplayName("예상 대기시간 - 1그룹(1~100번)은 10초다")
    void calculateEstimatedWaitSec_first_group() {
        // given & when & then
        assertThat(queueDomainService.calculateEstimatedWaitSec(1)).isEqualTo(10);
        assertThat(queueDomainService.calculateEstimatedWaitSec(50)).isEqualTo(10);
        assertThat(queueDomainService.calculateEstimatedWaitSec(100)).isEqualTo(10);
    }

    @Test
    @DisplayName("예상 대기시간 - 2그룹(101~200번)은 20초다")
    void calculateEstimatedWaitSec_second_group() {
        // given & when & then
        assertThat(queueDomainService.calculateEstimatedWaitSec(101)).isEqualTo(20);
        assertThat(queueDomainService.calculateEstimatedWaitSec(200)).isEqualTo(20);
    }

    @Test
    @DisplayName("예상 대기시간 - 3그룹(201~300번)은 30초다")
    void calculateEstimatedWaitSec_third_group() {
        // given & when & then
        assertThat(queueDomainService.calculateEstimatedWaitSec(201)).isEqualTo(30);
        assertThat(queueDomainService.calculateEstimatedWaitSec(300)).isEqualTo(30);
    }
}
