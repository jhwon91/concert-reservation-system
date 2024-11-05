package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import lombok.Builder;

import java.util.UUID;

public class TokenResult{

    @Builder
    public record IssueToken(
            UUID token,
            TokenStatus status,
            Integer position
    ) {
        public static IssueToken from(Queue queue, int position) {
            return IssueToken.builder()
                    .token(queue.getToken())
                    .status(queue.getStatus())
                    .position(position)
                    .build();
        }
    }

    @Builder
    public record TokenStatusResult(
            TokenStatus status,
            Integer position
    ) {
        public static TokenStatusResult from(Queue queue, int position) {
            return TokenStatusResult.builder()
                    .status(queue.getStatus())
                    .position(position)
                    .build();
        }
    }
}




