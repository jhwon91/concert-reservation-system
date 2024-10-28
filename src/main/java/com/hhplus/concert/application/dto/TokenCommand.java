package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class TokenCommand{

    @Builder
    public record issueTokenCommand(
            Long userId
    ){ }

    @Builder
    public record tokenStatusCommand(
            UUID token
    ){ }
}
