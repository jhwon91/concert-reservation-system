package com.hhplus.concert.testFixture;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueueFixture {

    public static Queue createQueue(User user, TokenStatus status, LocalDateTime time) {
        return Queue.builder()
                .id(1L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(status)
                .enteredAt(time)
                .lastRequestedAt(time)
                .expiredAt(null)
                .build();
    }

    public static List<Queue> ondWaitQueueList (User user, LocalDateTime time){
        Queue queue1 =  Queue.builder()
                .id(1L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.WAIT)
                .enteredAt(time.minusHours(5))
                .lastRequestedAt(time.minusHours(5))
                .expiredAt(null)
                .build();
        Queue queue2 =  Queue.builder()
                .id(2L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.EXPIRED)
                .enteredAt(time.minusDays(1))
                .lastRequestedAt(time.minusDays(1))
                .expiredAt(time.minusDays(1))
                .build();
        Queue queue3 =  Queue.builder()
                .id(3L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.EXPIRED)
                .enteredAt(time.minusDays(2))
                .lastRequestedAt(time.minusDays(2))
                .expiredAt(time.minusDays(2))
                .build();

        return List.of(queue1, queue2, queue3);
    }

    public static List<Queue> userNotWaitQueueList (User user, LocalDateTime time){
        Queue queue1 =  Queue.builder()
                .id(1L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.EXPIRED)
                .enteredAt(time.minusHours(5))
                .lastRequestedAt(time.minusHours(5))
                .expiredAt(time.minusHours(5))
                .build();
        Queue queue2 =  Queue.builder()
                .id(2L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.EXPIRED)
                .enteredAt(time.minusDays(1))
                .lastRequestedAt(time.minusDays(1))
                .expiredAt(time.minusDays(1))
                .build();
        Queue queue3 =  Queue.builder()
                .id(3L)
                .userId(user.getId())
                .token(UUID.randomUUID())
                .status(TokenStatus.EXPIRED)
                .enteredAt(time.minusDays(2))
                .lastRequestedAt(time.minusDays(2))
                .expiredAt(time.minusDays(2))
                .build();

        return List.of(queue1, queue2, queue3);
    }

    public static List<Queue> WaitQueueList (){
        List<Queue> queueList = new ArrayList<>();

        for(long i = 1; i <=10; i++){
            Queue queue =  Queue.builder()
                    .id(i)
                    .userId(i)
                    .token(UUID.randomUUID())
                    .status(TokenStatus.WAIT)
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .build();

            queueList.add(queue);
        }

        return queueList;
    }
}
