package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.enums.TokenStatus;
import lombok.Builder;

import java.util.UUID;

public class TokenResult{

    @Builder
    public record issueToken(
            UUID token,
            TokenStatus status,
            Integer position
    ) {
        public static issueToken from(Queue queue, int position) {
            return issueToken.builder()
                    .token(queue.getToken())
                    .status(queue.getStatus())
                    .position(position)
                    .build();
        }
    }

    @Builder
    public record tokenStatus(
            TokenStatus status,
            Integer position
    ) {
        public static tokenStatus from(Queue queue, int position) {
            return tokenStatus.builder()
                    .status(queue.getStatus())
                    .position(position)
                    .build();
        }
    }
}




