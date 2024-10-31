package com.hhplus.concert.application;

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
    public User pointSearch(long userId){
        User user = userService.findUserById(userId);
        return user;
    }

    /**
     * 잔액 충전
     */
    public User chargePoint(long userId, long amount){
        return  userService.chargePoint(userId,amount);
    }

    /**
     * 특정 유저의 잔액 충전/이용 내역을 조회하는 기능
     */
    public List<PointHistory> history(long userId){
        User user = userService.findUserById(userId);
        return pointHistoryService.getUserHistory(user.getId());
    }
}
