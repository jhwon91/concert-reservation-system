package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.dto.TokenCommand;

public record TokenCriteria(
        Long user_id
) {
    public TokenCommand toCommand() {
        return new TokenCommand(
                this.user_id
        );
    }
}
