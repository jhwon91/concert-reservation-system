package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.ConcertCommand;
import com.hhplus.concert.domain.entity.*;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertDetailService concertDetailService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private SeatService seatService;

    private UUID token;
    private Long userId;
    private Long concertDetailId;
    private Long seatId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("testUser")
                .point(100L)
                .build();
        User savedUser = userService.save(user);
        userId = savedUser.getId();

        Concert concert = Concert.builder()
                .title("testConcert")
                .build();
        Concert savedConcert = concertService.save(concert);

        ConcertDetails concertDetails = ConcertDetails.builder()
                .concertId(savedConcert.getId())
                .maxSeat(100L)
                .build();
        ConcertDetails savedConcertDetails = concertDetailService.save(concertDetails);
        concertDetailId = savedConcertDetails.getId();

        Seat seat = Seat.builder()
                .concertDetailId(concertDetailId)
                .seatNumber(1)
                .status(SeatStatus.AVAILABLE)
                .build();
        Seat savedSeat = seatService.save(seat);
        seatId = savedSeat.getId();

        Queue queue = queueService.addToQueue(savedUser);
        token = queue.getToken();
    }
    @Test
    void 동시에_좌석_예약을_시도할_경우_단일_성공만_허용됨() throws InterruptedException {
        // given
        int threadCount = 10000; // 동시에 요청할 스레드 수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ConcertCommand.reserveSeat command = new ConcertCommand.reserveSeat(
                token, userId, concertDetailId, seatId);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    concertFacade.reserveSeat(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Seat reservedSeat = seatService.getSeat(seatId);
        assertEquals(SeatStatus.TEMPORARY_ALLOCATED, reservedSeat.getStatus());
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failCount.get());
    }
}