package com.hhplus.concert.user.presentation.dto;

import com.hhplus.concert.user.application.dto.UserPointResult;

import java.math.BigDecimal;
import java.util.UUID;

public record UserPointResponse(UUID userId, BigDecimal balance) {

    public static UserPointResponse from(UserPointResult result) {
        return new UserPointResponse(result.userId(), result.balance());
    }
}
