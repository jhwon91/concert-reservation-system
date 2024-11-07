package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.repository.QueueRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import com.hhplus.concert.testFixture.QueueFixture;
import com.hhplus.concert.testFixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueueServiceTest {
    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private QueueService queueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 대기열에추가_사용자가이미대기중인큐가있는경우() {
        User waitUser = UserFixture.createUser(1L,"test", 100L);
        List<Queue> queue = QueueFixture.ondWaitQueueList(waitUser, LocalDateTime.now());

        when(queueRepository.findByUserId(waitUser.getId())).thenReturn(queue);

        Queue result = queueService.addToQueue(waitUser);

        assertEquals(TokenStatus.WAIT, result.getStatus());
    }

    @Test
    void 대기열에추가_사용자가대기중인큐없고_활성상태_자리가_남아_있을때_활성상태로추가되어야한다() {
        User user = UserFixture.createUser(1L,"test", 100L);
        List<Queue> queue = QueueFixture.userNotWaitQueueList(user, LocalDateTime.now());

        when(queueRepository.findByUserId(user.getId())).thenReturn(queue);
        when(queueRepository.countByStatus(TokenStatus.ACTIVE)).thenReturn(5L);
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Queue result = queueService.addToQueue(user);

        assertNotNull(result);
        assertEquals(TokenStatus.ACTIVE, result.getStatus());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(null, result.getExpiredAt());
        verify(queueRepository).save(any(Queue.class));

    }

    @Test
    void 대기열에추가_사용자가대기중인큐가없을때_활성상태_자리가_없을때_대기상태로추가되어야한다() {

        User user = UserFixture.createUser(1L,"test", 100L);
        List<Queue> queue = QueueFixture.userNotWaitQueueList(user, LocalDateTime.now());

        when(queueRepository.findByUserId(user.getId())).thenReturn(queue);
        when(queueRepository.countByStatus(TokenStatus.ACTIVE)).thenReturn(11L);
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Queue result = queueService.addToQueue(user);

        assertNotNull(result);
        assertEquals(TokenStatus.WAIT, result.getStatus());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(null, result.getExpiredAt());
        verify(queueRepository).save(any(Queue.class));
    }

    @Test
    void 대기순서조회_사용자가_대기_중_일_때() {

        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.WAIT,LocalDateTime.now());

        when(queueRepository.countByIdLessThanAndStatus(queue.getId(), TokenStatus.WAIT)).thenReturn(5);

        int position = queueService.getQueuePosition(queue);

        assertEquals(6, position); // 자기 자신 포함하여 +1
    }

    @Test
    void 대기순서조회_사용자가_대기_중이_아닐_때() {
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue activeQueue = QueueFixture.createQueue(user,TokenStatus.ACTIVE,LocalDateTime.now());
        Queue expiredQueue = QueueFixture.createQueue(user,TokenStatus.EXPIRED,LocalDateTime.now());

        int position1 = queueService.getQueuePosition(activeQueue);
        int position2 = queueService.getQueuePosition(expiredQueue);

        assertEquals(0, position1);
        assertEquals(0, position2);
    }

    @Test
    void 토큰으로_큐조회_토큰이_존재할_때() {

        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.ACTIVE,LocalDateTime.now());

        UUID token = queue.getToken();
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(queue));

        Queue result = queueService.getQueueByToken(token);

        assertNotNull(result);
        assertEquals(queue, result);
    }

    @Test
    void 토큰으로_큐조회_토큰이_존재하지_않을_때() {
        when(queueRepository.findByToken(any())).thenReturn(Optional.empty());

        UUID token = UUID.randomUUID();
        CoreException exception = assertThrows(CoreException.class, () -> queueService.getQueueByToken(token));

        assertEquals(ErrorType.TOKEN_NOT_FOUND,exception.getErrorType());
        assertEquals(token,exception.getPayload());
    }

    @Test
    void 토큰과_사용자검증_사용자가_일치할때() {
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.ACTIVE,LocalDateTime.now());
        assertDoesNotThrow(() -> queueService.validationUserOfToken(queue, user));
    }

    @Test
    void 토큰과_사용자검증_사용자가_일치하지않을때() {
        User user1 = UserFixture.createUser(1L,"test", 100L);
        User user2 = UserFixture.createUser(2L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user1,TokenStatus.ACTIVE,LocalDateTime.now());

        CoreException exception = assertThrows(CoreException.class, () -> queueService.validationUserOfToken(queue, user2));

        assertEquals(ErrorType.USER_NOT_MATCHED_TOKEN,exception.getErrorType());
        assertEquals(user2.getId(),exception.getPayload());
    }

    @Test
    void 활성토큰검증_토큰이_활성상태_일_때() {
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.ACTIVE,LocalDateTime.now());

        assertDoesNotThrow(() -> queueService.validateActiveToken(queue));
    }

    @Test
    void 활성토큰검증_토큰이_활성상태가_아닐_때() {
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.WAIT,LocalDateTime.now());

        CoreException exception = assertThrows(CoreException.class, () -> queueService.validateActiveToken(queue));

        assertEquals(ErrorType.TOKEN_NOT_ACTIVE,exception.getErrorType());
        assertEquals(queue.getToken(),exception.getPayload());
    }

    @Test
    void 큐_상태변경_Expired_변경() {
        LocalDateTime expiredAt = LocalDateTime.now();
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.WAIT,LocalDateTime.now());

        // When
        Queue result = queueService.changeQueueStatus(queue, TokenStatus.EXPIRED, Optional.of(expiredAt));

        // Then
        assertEquals(TokenStatus.EXPIRED, result.getStatus());
        assertEquals(expiredAt, result.getExpiredAt());
    }

    @Test
    void 큐_상태변경_Active_변경() {
        LocalDateTime expiredAt = LocalDateTime.now();
        User user = UserFixture.createUser(1L,"test", 100L);
        Queue queue = QueueFixture.createQueue(user,TokenStatus.WAIT,LocalDateTime.now());

        Queue result = queueService.changeQueueStatus(queue, TokenStatus.ACTIVE, Optional.empty());

        // Then
        assertEquals(TokenStatus.ACTIVE, result.getStatus());
        assertNull(result.getExpiredAt());
    }






    }

