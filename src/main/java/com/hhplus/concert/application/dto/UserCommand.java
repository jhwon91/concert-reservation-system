package com.hhplus.concert.application.dto;

import lombok.Builder;

public class UserCommand {
    @Builder
    public record SearchPoint(
            Long userId
    ){ }

    @Builder
    public record ChargePoint(
            Long userId,
            Long amount
    ){ }

    @Builder
    public record History(
            Long userId
    ){ }
}


