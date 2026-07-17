package com.hhplus.concert.user.application.dto;

import com.hhplus.concert.user.domain.UserPoint;

import java.math.BigDecimal;
import java.util.UUID;

public record UserPointResult(UUID userId, BigDecimal balance) {

    public static UserPointResult from(UserPoint userPoint) {
        return new UserPointResult(userPoint.getUserId(), userPoint.getBalance());
    }
}
