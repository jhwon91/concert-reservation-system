package com.hhplus.concert.application.dto;

import lombok.Builder;
public class TokenCommand{

    @Builder
    public record issueTokenCommand(
            Long userId
    ){ }

    @Builder
    public record tokenStatusCommand(
            String token
    ){ }
}
