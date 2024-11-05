package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.TokenCommand;
import com.hhplus.concert.application.dto.TokenResult;
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
    public TokenResult.IssueToken issueToken(TokenCommand.IssueToken command) {
        User user = userService.findUserById(command.userId());
        Queue queue = queueService.addToQueue(user);
        int position = queueService.getQueuePosition(queue);
        return TokenResult.IssueToken.from(queue, position);
    }

    /**
     * 대기열 상태 확인
     */
    public TokenResult.TokenStatusResult getTokenStatus(TokenCommand.TokenStatus command) {
        Queue queue = queueService.getQueueByToken(command.token());
        int position = queueService.getQueuePosition(queue);
        return TokenResult.TokenStatusResult.from(queue, position);
    }
}
