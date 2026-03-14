package com.hhplus.concert.user.application.dto;

import com.hhplus.concert.user.domain.UserPoint;

import java.math.BigDecimal;
import java.util.UUID;

public record UserPointResponse(UUID userId, BigDecimal balance) {

    public static UserPointResponse from(UserPoint userPoint) {
        return new UserPointResponse(userPoint.getUserId(), userPoint.getBalance());
    }
}
