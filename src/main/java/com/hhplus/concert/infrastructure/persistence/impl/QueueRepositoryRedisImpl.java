package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import com.hhplus.concert.infrastructure.persistence.JpaQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@Qualifier("redisQueueRepository")
public class QueueRepositoryRedisImpl implements QueueRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WAITING_QUEUE_KEY = "waitingQueue";
    private static final String ACTIVE_QUEUE_KEY_PREFIX = "activeUser:";
    private static final long ACTIVE_USER_TTL = 60L; // 1분
    private final JpaQueueRepository jpaQueueRepository;

    @Autowired
    public QueueRepositoryRedisImpl(RedisTemplate<String, Object> redisTemplate, JpaQueueRepository jpaQueueRepository) {
        this.redisTemplate = redisTemplate;
        this.jpaQueueRepository = jpaQueueRepository;
    }

    @Override
    @Transactional
    public Queue save(Queue queue) {
        // 데이터베이스에 저장
        Queue savedQueue = jpaQueueRepository.save(queue);

        // Redis에 저장
        if (queue.getStatus() == TokenStatus.WAIT) {
            addToWaitingQueue(queue);
        } else if (queue.getStatus() == TokenStatus.ACTIVE) {
            moveToActiveQueue(queue);
        }

        String redisKey = "queue:token:" + queue.getToken().toString();
        redisTemplate.opsForValue().set(redisKey, queue, ACTIVE_USER_TTL, TimeUnit.SECONDS);


        return savedQueue;
    }

    @Override
    public List<Queue> findByUserId(long userId) {
        return jpaQueueRepository.findByUserId(userId);
    }

    @Override
    public long countByStatus(TokenStatus status) {
        if (status == TokenStatus.WAIT) {
            // Redis의 대기열 크기 반환
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            Long size = zSetOps.size(WAITING_QUEUE_KEY);
            return size != null ? size : 0;
        } else {
            // 데이터베이스에서 상태별 개수 조회
            return jpaQueueRepository.countByStatusWithLock(status);
        }
    }

    @Override
    public Optional<Queue> findByToken(UUID token) {
        // Redis에서 조회
        String redisKey = "queue:token:" + token.toString();
        Queue queue = (Queue) redisTemplate.opsForValue().get(redisKey);

        if (queue != null) {
            return Optional.of(queue);
        }

        // 데이터베이스에서 조회
        Optional<Queue> dbQueue = jpaQueueRepository.findByToken(token);

        // Redis에 캐싱
        dbQueue.ifPresent(q -> redisTemplate.opsForValue().set(redisKey, q));

        return dbQueue;
    }

    @Override
    public int countByIdLessThanAndStatus(Long id, TokenStatus status) {
        return jpaQueueRepository.countByIdLessThanAndStatus(id, status);
    }

    @Override
    public boolean exists(UUID token) {
        return jpaQueueRepository.existsByToken(token);
    }

    @Override
    public List<Queue> findByStatus(TokenStatus status, Pageable pageable) {
        if (status == TokenStatus.WAIT) {
            // Redis에서 대기열 조회
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            Set<Object> members = zSetOps.range(WAITING_QUEUE_KEY, pageable.getOffset(), pageable.getOffset() + pageable.getPageSize() - 1);
            if (members != null) {
                return members.stream()
                        .map(member -> {
                            // 필요한 경우 데이터베이스에서 추가 정보 조회
                            Long userId = Long.parseLong(member.toString());
                            return jpaQueueRepository.findByUserId(userId).stream().findFirst().orElse(null);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } else {
            // 데이터베이스에서 조회
            return jpaQueueRepository.findByStatus(status, pageable);
        }
    }

    @Override
    public List<Queue> findActiveQueuesToExpire(TokenStatus status, LocalDateTime expirationTime) {
        return jpaQueueRepository.findActiveQueuesToExpire(status, expirationTime);
    }

    private void addToWaitingQueue(Queue queue) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        double score = System.currentTimeMillis();
        zSetOps.add(WAITING_QUEUE_KEY, queue.getUserId().toString(), score);
    }

    private void moveToActiveQueue(Queue queue) {
        // 대기열에서 제거
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(WAITING_QUEUE_KEY, queue.getUserId().toString());

        // 활성 큐에 추가 (TTL 설정)
        String activeUserKey = ACTIVE_QUEUE_KEY_PREFIX + queue.getUserId();
        redisTemplate.opsForValue().set(activeUserKey, "active", ACTIVE_USER_TTL, TimeUnit.SECONDS);
    }
}
