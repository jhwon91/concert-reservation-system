package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.application.dto.TokenCriteria;
import lombok.Builder;

@Builder
public record TokenRequestDTO(
        Long user_id
) {
    public TokenCriteria toCriteria() {
        return new TokenCriteria(
            this.user_id
        );
    }
}
