package com.hhplus.concert.integration.concurrency;

import com.hhplus.concert.user.application.dto.ChargePointCommand;
import com.hhplus.concert.user.application.service.ChargePointService;
import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.infrastructure.JpaUserPointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PointChargeConcurrencyTest {

    @Autowired
    private ChargePointService chargePointService;

    @Autowired
    private JpaUserPointRepository jpaUserPointRepository;

    private UUID userId;

    private static final int THREAD_COUNT = 10;
    private static final BigDecimal CHARGE_AMOUNT = BigDecimal.valueOf(10_000);

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UserPoint userPoint = new UserPoint(userId);
        jpaUserPointRepository.save(userPoint);
    }

    @AfterEach
    void tearDown() {
        jpaUserPointRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 포인트 충전 요청 시 모두 성공하고 잔액이 정확해야 한다")
    void 동시에_포인트_충전_요청_시_모두_성공하고_잔액이_정확해야_한다() throws InterruptedException {
        // given
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    chargePointService.charge(new ChargePointCommand(userId, CHARGE_AMOUNT));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
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
        assertThat(successCount.get()).isEqualTo(THREAD_COUNT);
        assertThat(failCount.get()).isEqualTo(0);

        UserPoint finalUserPoint = jpaUserPointRepository.findByUserId(userId).orElseThrow();
        BigDecimal expectedBalance = CHARGE_AMOUNT.multiply(BigDecimal.valueOf(THREAD_COUNT));
        assertThat(finalUserPoint.getBalance()).isEqualByComparingTo(expectedBalance);
    }
}
