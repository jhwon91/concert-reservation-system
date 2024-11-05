package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class TokenCommand{

    @Builder
    public record issueToken(
            Long userId
    ){ }

    @Builder
    public record tokenStatus(
            UUID token
    ){ }
}
