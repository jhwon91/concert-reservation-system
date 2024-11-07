package com.hhplus.concert.domain.support.scheduler;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.service.QueueService;
import com.hhplus.concert.testFixture.QueueFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueueSchedulerTest {
    @Mock
    private QueueService queueService;

    @InjectMocks
    private QueueScheduler queueScheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 활성화할_사용자가_있을_때(){
        List<Queue> waitQueue = QueueFixture.WaitQueueList();

        when(queueService.getMaxActiveUsers()).thenReturn(10);
        when(queueService.countActiveUsers()).thenReturn(0L);
        when(queueService.findNextWaitingQueues(anyLong())).thenReturn(waitQueue);

        queueScheduler.activeQueue();

        verify(queueService, times(1)).getMaxActiveUsers();
        verify(queueService, times(1)).countActiveUsers();
        verify(queueService, times(1)).findNextWaitingQueues(10L);
        verify(queueService, times(10)).changeQueueStatus(any(Queue.class), eq(TokenStatus.ACTIVE), eq(Optional.empty()));
    }

    @Test
    void 활성화할_사용자가_없을_때(){
        when(queueService.getMaxActiveUsers()).thenReturn(10);
        when(queueService.countActiveUsers()).thenReturn(10L);

        queueScheduler.activeQueue();

        verify(queueService, times(1)).getMaxActiveUsers();
        verify(queueService, times(1)).countActiveUsers();
        verify(queueService, never()).findNextWaitingQueues(anyLong());
        verify(queueService, never()).changeQueueStatus(any(), any(), any());
    }

    @Test
    void 만료시킬_큐가_있을_때(){

    }

    @Test
    void 만료시킬_큐가_없을_때(){

    }
}