package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class UserCommand {
    @Builder
    public record searchPoint(
            Long userId
    ){ }

    @Builder
    public record chargePoint(
            Long userId,
            Long amount
    ){ }

    @Builder
    public record history(
            Long userId
    ){ }
}


