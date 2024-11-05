package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class TokenCommand{

    @Builder
    public record IssueToken(
            Long userId
    ){ }

    @Builder
    public record TokenStatus(
            UUID token
    ){ }
}
