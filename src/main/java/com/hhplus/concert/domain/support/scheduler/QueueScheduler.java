package com.hhplus.concert.domain.support.scheduler;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.service.QueueService;
import com.hhplus.concert.domain.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class QueueScheduler {

    private final QueueService queueService;

    public QueueScheduler(QueueService queueService, UserService userService) {
        this.queueService = queueService;
    }

    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void activeQueue () {
        long maxActiveUsers = queueService.getMaxActiveUsers();
        long currentActiveUsers = queueService.countActiveUsers();

        long usersToActivate = maxActiveUsers - currentActiveUsers;
        if (usersToActivate <= 0) {
            return;
        }

        List<Queue> waitingQueues = queueService.findNextWaitingQueues(usersToActivate);

        for (Queue queue : waitingQueues) {
            queueService.changeQueueStatus(queue, TokenStatus.ACTIVE, Optional.empty());
        }
    }

    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void expiredQueue () {
        LocalDateTime expirationTime = LocalDateTime.now();

        List<Queue> queuesToExpire = queueService.findActiveQueuesToExpire(TokenStatus.ACTIVE, expirationTime);

        for (Queue queue : queuesToExpire) {
            queue.updateStatus(TokenStatus.EXPIRED, Optional.of(LocalDateTime.now()));
        }
    }

}
