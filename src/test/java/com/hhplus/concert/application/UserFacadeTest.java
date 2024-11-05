package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.UserCommand;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.PointHistoryRepository;
import com.hhplus.concert.domain.repository.UserRepository;
import com.hhplus.concert.domain.service.PointHistoryService;
import com.hhplus.concert.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User saveuser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("testUser")
                .point(100L)
                .build();
        saveuser = userRepository.save(user);

    }
    @Test
    void 동시에_포인트를_충전할_경우_하나이상_성공해야함() throws InterruptedException {
        // given
        int threadCount = 10;

        UserCommand.ChargePoint command =  UserCommand.ChargePoint.builder()
                .userId(saveuser.getId())
                .amount(1000L)
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userFacade.chargePoint(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
//                    e.printStackTrace(); // 예외 로깅
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        User resultUser = userService.findUserById(saveuser.getId());

        assertEquals(1,successCount.get());
        assertEquals(threadCount - 1,failCount.get());
        assertEquals(1100L,resultUser.getPoint());
    }

}