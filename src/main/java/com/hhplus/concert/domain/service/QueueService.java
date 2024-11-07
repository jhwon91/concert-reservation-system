package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    private static final int MAX_ACTIVE_USERS = 10; // 예시: 최대 활성 사용자 수

    @Autowired
    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }


    @Transactional
    public Queue addToQueue(User user) {
        //활성 사용자 확인
        long activeCount = queueRepository.countByStatus(TokenStatus.ACTIVE);

        //사용자 아이디로 대기열의 상태 확인
        List<Queue> userQueueStatus = queueRepository.findByUserId(user.getId());

        //만료 시간이 null 있으면 상태값 반환(wait, active), 없으면 active 생성
        Queue resultQueue = userQueueStatus.stream()
                .filter(queue -> queue.getEnteredAt() == null)
                .findFirst()
                .orElse(null);

        if(resultQueue != null) { return resultQueue; }

        Queue queue = Queue.builder()
                .userId(user.getId())
                .token(UUID.randomUUID())
                .enteredAt(LocalDateTime.now())
                .lastRequestedAt(LocalDateTime.now())
                .status(activeCount < MAX_ACTIVE_USERS ? TokenStatus.ACTIVE : TokenStatus.WAIT)
                .build();

        queue = queueRepository.save(queue);

        return queue;
    }

    public int getQueuePosition(Queue queue) {
        int position = 0;
        if(queue.getStatus() == TokenStatus.WAIT){
            // 대기열 번호 계산 (create 보다 작고 상태가 wait 인것) + 1
            position = queueRepository.countByIdLessThanAndStatus(queue.getId(), queue.getStatus()) + 1;
        }
        return position;
    }

    public Queue getQueueByToken(UUID token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, token));
    }

    public void validationUser(Queue queue, User user) {
        if(queue.getUserId() != user.getId()){
            throw new CoreException(ErrorType.USER_NOT_MATCHED_TOKEN, user.getId());
        }
    }

    //토큰 검증
    public void validateActiveToken(Queue queue){
        queue.validateActiveStatus();
    }

    public Queue changeQueueStatus(Queue queue, TokenStatus status, Optional<LocalDateTime> expiredAtOpt) {
        return Queue.builder()
                .id(queue.getId())
                .token(queue.getToken())
                .userId(queue.getUserId())
                .status(status)
                .enteredAt(queue.getEnteredAt())
                .lastRequestedAt(queue.getLastRequestedAt())
                .expiredAt(status == TokenStatus.EXPIRED ? expiredAtOpt.orElse(LocalDateTime.now()) : null)
                .build();
    }

    public Queue save(Queue queue){
        return queueRepository.save(queue);
    }
}
