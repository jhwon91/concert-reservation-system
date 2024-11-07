package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.TokenCommand;
import com.hhplus.concert.application.dto.TokenResult;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TokenStatus;
import com.hhplus.concert.domain.service.UserService;
import com.hhplus.concert.testFixture.UserFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TokenFacadeTest {
    @Autowired
    private TokenFacade tokenFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static List<User> testUser = new ArrayList<>();

    @BeforeAll
    static void setup(@Autowired UserService userService) {
        List<User> users = UserFixture.createdUserList(11L);

        for(User user:users){
            User savedUser = userService.save(user);
            testUser.add(savedUser);
        }
    }

    @AfterEach
    void cleanRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 사용자_11명_추가하고_활성_대기_상태_확인() throws InterruptedException {
        // Given
        List<TokenResult.IssueToken> issueResults = new ArrayList<>();

        for (User user : testUser) {
            TokenCommand.IssueToken command = new TokenCommand.IssueToken(user.getId());
            TokenResult.IssueToken result = tokenFacade.issueToken(command);
            issueResults.add(result);
        }

        // 스케줄러가 실행될 때까지 대기 (예: 2초 대기)
        Thread.sleep(2000); // 스케줄러 주기에 맞게 조정

        // When
        int activeUserCount = 0;
        int waitingUserCount = 0;

        for (TokenResult.IssueToken issueResult : issueResults) {
            TokenCommand.TokenStatus statusCommand = new TokenCommand.TokenStatus(issueResult.token());
            TokenResult.TokenStatusResult statusResult = tokenFacade.getTokenStatus(statusCommand);

            if (statusResult.status() == TokenStatus.ACTIVE) {
                activeUserCount++;
            } else if (statusResult.status() == TokenStatus.WAIT) {
                waitingUserCount++;
            }
        }

        // Then
        assertEquals(10, activeUserCount, "활성 사용자 수는 10명이어야 합니다.");
        assertEquals(1, waitingUserCount, "대기 중인 사용자 수는 1명이어야 합니다.");
    }
}