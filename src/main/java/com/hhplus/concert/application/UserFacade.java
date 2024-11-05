package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.UserCommand;
import com.hhplus.concert.application.dto.UserResult;
import com.hhplus.concert.domain.entity.PointHistory;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.service.PointHistoryService;
import com.hhplus.concert.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserFacade {
    private final UserService userService;
    private final PointHistoryService pointHistoryService;

    @Autowired
    public UserFacade(UserService userService, PointHistoryService pointHistoryService) {
        this.userService = userService;
        this.pointHistoryService = pointHistoryService;
    }

    /**
     * 사용자 잔액 조회
     */
    public UserResult.searchPoint searchPoint(UserCommand.searchPoint command){
        User user = userService.findUserById(command.userId());
        return UserResult.searchPoint.from(user);
    }

    /**
     * 잔액 충전
     */
    public UserResult.chargePoint chargePoint(UserCommand.chargePoint command){
        User user = userService.chargePoint(command.userId(), command.amount());
        return  UserResult.chargePoint.from(user);
    }

    /**
     * 특정 유저의 잔액 충전/이용 내역을 조회하는 기능
     */
    public UserResult.history history(UserCommand.history command){
        User user = userService.findUserById(command.userId());
        List<PointHistory> pointHistory = pointHistoryService.getUserHistory(user.getId());
        return UserResult.history.from(user, pointHistory);
    }
}
