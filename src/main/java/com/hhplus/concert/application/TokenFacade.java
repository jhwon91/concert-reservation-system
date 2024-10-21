package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.TokenCriteria;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.service.UserService;
import com.hhplus.concert.domain.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenFacade {
    private final UserService userService;
    private final QueueService queueService;

    @Autowired
    public TokenFacade(UserService userService, QueueService queueService) {
        this.userService = userService;
        this.queueService = queueService;
    }

    /**
     * 유저 토큰 발급
     */
    public Queue issueToken(TokenCriteria tokenCriteria) {
        User user = userService.findUserById(tokenCriteria.user_id());
        return queueService.addToQueue(user);
    }

    /**
     * 대기열 상태 확인
     */
    public Queue getTokenStatus(String token) {
        return queueService.getQueueStatus(token);
    }

}
