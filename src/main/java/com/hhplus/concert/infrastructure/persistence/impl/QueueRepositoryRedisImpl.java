package com.hhplus.concert.infrastructure.persistence.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("redisQueueRepository")
public class QueueRepositoryRedisImpl implements QueueRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String QUEUE_KEY = "queue:%s";
    private static final String USER_QUEUE_KEY = "user:queue:%d";
    private static final String STATUS_QUEUE_KEY = "status:queue:%s";
    private static final String QUEUE_ID_COUNTER = "queue:id:counter";

    @Autowired
    public QueueRepositoryRedisImpl(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    private Long generateId() {
        Long id = redisTemplate.opsForValue().increment(QUEUE_ID_COUNTER);
        if (id == null) {
            throw new RuntimeException("Failed to generate queue ID");
        }
        return id;
    }

    @Override
    public Queue save(Queue queue) {
        try {

            if (queue.getId() == null) {
                Long newId = generateId();
                Field idField = Queue.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(queue, newId);
            }

            if (queue.getCreatedAt() == null) {
                queue.setCreateAt();
            }

            String queueJson = objectMapper.writeValueAsString(queue);

            String queueKey = String.format(QUEUE_KEY, queue.getToken());
            String userQueueKey = String.format(USER_QUEUE_KEY, queue.getUserId());
            String statusQueueKey = String.format(STATUS_QUEUE_KEY, queue.getStatus());

            redisTemplate.opsForValue().set(queueKey, queueJson);
            redisTemplate.opsForSet().add(userQueueKey, queue.getToken().toString());
            redisTemplate.opsForZSet().add(statusQueueKey, queue.getToken().toString(), queue.getId());

            return queue;
        } catch (JsonProcessingException e) {
            throw new RuntimeException( e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Queue> findByUserId(long id) {
        String userQueueKey = String.format(USER_QUEUE_KEY, id);
        Set<String> tokenSet = redisTemplate.opsForSet().members(userQueueKey);

        if (tokenSet == null || tokenSet.isEmpty()) {
            return Collections.emptyList();
        }

        return tokenSet.stream()
                .map(token -> findByToken(UUID.fromString(token)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(TokenStatus status) {
        String statusQueueKey = String.format(STATUS_QUEUE_KEY, status);
        Long size = redisTemplate.opsForZSet().size(statusQueueKey);
        return size != null ? size : 0;
    }

    @Override
    public Optional<Queue> findByToken(UUID token) {
        String queueKey = String.format(QUEUE_KEY, token);
        String queueJson = redisTemplate.opsForValue().get(queueKey);

        if (queueJson == null) {
            return Optional.empty();
        }

        try {
            Queue queue = objectMapper.readValue(queueJson, Queue.class);
            return Optional.of(queue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Queue deserialization failed", e);
        }
    }

    @Override
    public int countByIdLessThanAndStatus(Long id, TokenStatus status) {
        String statusQueueKey = String.format(STATUS_QUEUE_KEY, status);
        Long count = redisTemplate.opsForZSet().count(statusQueueKey, 0, id);
        return count != null ? count.intValue() : 0;
    }

    @Override
    public boolean exists(UUID token) {
        String queueKey = String.format(QUEUE_KEY, token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(queueKey));
    }

    @Override
    public List<Queue> findByStatus(TokenStatus status, Pageable pageable) {
        String statusQueueKey = String.format(STATUS_QUEUE_KEY, status);
        Set<String> tokens = redisTemplate.opsForZSet().range(
                statusQueueKey,
                pageable.getOffset(),
                pageable.getOffset() + pageable.getPageSize() - 1
        );

        if (tokens == null || tokens.isEmpty()) {
            return Collections.emptyList();
        }

        return tokens.stream()
                .map(token -> findByToken(UUID.fromString(token)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
