package com.hhplus.concert.integration.concurrency;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import com.hhplus.concert.concert.infrastructure.JpaSeatRepository;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import com.hhplus.concert.queue.infrastructure.JpaQueueTokenRepository;
import com.hhplus.concert.reservation.application.dto.CreateReservationCommand;
import com.hhplus.concert.reservation.application.service.CreateReservationService;
import com.hhplus.concert.reservation.infrastructure.JpaReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeatReservationConcurrencyTest {

    @Autowired
    private CreateReservationService createReservationService;

    @Autowired
    private JpaSeatRepository jpaSeatRepository;

    @Autowired
    private JpaQueueTokenRepository jpaQueueTokenRepository;

    @Autowired
    private JpaReservationRepository jpaReservationRepository;

    private UUID seatId;
    private List<String> tokenValues;

    private static final int THREAD_COUNT = 50;

    @BeforeEach
    void setUp() {
        tokenValues = new ArrayList<>();

        Seat seat = new Seat(UUID.randomUUID(), SeatNumber.of(1), BigDecimal.valueOf(99_000));
        jpaSeatRepository.save(seat);
        seatId = seat.getSeatId();

        for (int i = 0; i < THREAD_COUNT; i++) {
            UUID userId = UUID.randomUUID();
            QueueToken token = new QueueToken(userId, TokenValue.generate(), i + 1, (i + 1) * 10);
            token.activate();
            jpaQueueTokenRepository.save(token);
            tokenValues.add(token.getTokenValue());
        }
    }

    @AfterEach
    void tearDown() {
        jpaReservationRepository.deleteAll();
        jpaSeatRepository.deleteAll();
        jpaQueueTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 같은 좌석 예약 요청 시 하나만 성공한다")
    void 동시에_같은_좌석_예약_요청_시_하나만_성공() throws InterruptedException {
        // given
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final String tokenValue = tokenValues.get(i);
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    createReservationService.create(
                            new CreateReservationCommand(UUID.randomUUID(), seatId, tokenValue));
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    failCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // when
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(THREAD_COUNT - 1);

        Seat reservedSeat = jpaSeatRepository.findById(seatId).orElseThrow();
        assertThat(reservedSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }
}
