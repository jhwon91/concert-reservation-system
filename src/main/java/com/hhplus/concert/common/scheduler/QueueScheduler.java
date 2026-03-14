package com.hhplus.concert.common.scheduler;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueTokenRepository queueTokenRepository;

    @Value("${queue.max-active-users}")
    private int maxActiveUsers;

    @Value("${queue.activation-batch-size}")
    private int activationBatchSize;

    @Scheduled(fixedRateString = "${queue.scheduler.activation-interval}")
    @Transactional
    public void activateWaitingTokens() {
        long currentActiveCount = queueTokenRepository.countByStatus(QueueTokenStatus.ACTIVE);
        int availableSlots = (int) (maxActiveUsers - currentActiveCount);

        if (availableSlots <= 0) {
            return;
        }

        int toActivate = Math.min(availableSlots, activationBatchSize);
        List<QueueToken> waitingTokens = queueTokenRepository.findByStatusOrderByPositionAsc(QueueTokenStatus.WAITING)
                .stream()
                .limit(toActivate)
                .toList();

        waitingTokens.forEach(token -> {
            token.activate();
            queueTokenRepository.save(token);
        });

        if (!waitingTokens.isEmpty()) {
            log.debug("대기열 활성화: {}개 토큰 활성화 (현재 활성: {})", waitingTokens.size(), currentActiveCount + waitingTokens.size());
        }
    }

    @Scheduled(fixedRateString = "${queue.scheduler.expiration-interval}")
    @Transactional
    public void expireActiveTokens() {
        List<QueueToken> expiredTokens = queueTokenRepository.findExpiredTokens(LocalDateTime.now());

        expiredTokens.forEach(token -> {
            token.expire();
            queueTokenRepository.save(token);
        });

        if (!expiredTokens.isEmpty()) {
            log.debug("대기열 만료 처리: {}개 토큰 만료", expiredTokens.size());
        }
    }
}
