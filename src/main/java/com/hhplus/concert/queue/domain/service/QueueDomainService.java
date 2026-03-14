package com.hhplus.concert.queue.domain.service;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QueueDomainService {

    private static final int MAX_ACTIVE_USERS = 100;
    private static final int ACTIVATION_INTERVAL_SEC = 10;

    public QueueToken issueToken(UUID userId, int currentQueueSize) {
        int position = currentQueueSize + 1;
        int estimatedWaitSec = calculateEstimatedWaitSec(position);
        return new QueueToken(userId, TokenValue.generate(), position, estimatedWaitSec);
    }

    public boolean canActivate(QueueToken queueToken, int currentActiveCount) {
        return queueToken.getPosition() <= currentActiveCount;
    }

    /**
     * 은행 창구 방식 예상 대기시간 계산
     * - MAX_ACTIVE_USERS개의 슬롯이 동시에 처리됨
     * - 스케줄러가 ACTIVATION_INTERVAL_SEC마다 빈 슬롯을 채움
     * - 내 순번이 몇 번째 그룹에 속하는지로 대기시간 추정
     *
     * 예) position=150, MAX_ACTIVE=100, INTERVAL=10초
     *     → ceil(150/100) = 2그룹 → 20초 대기
     */
    public int calculateEstimatedWaitSec(int position) {
        int groups = (int) Math.ceil((double) position / MAX_ACTIVE_USERS);
        return groups * ACTIVATION_INTERVAL_SEC;
    }
}
