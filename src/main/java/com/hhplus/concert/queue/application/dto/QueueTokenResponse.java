package com.hhplus.concert.queue.application.dto;

import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;

public record QueueTokenResponse(
        String tokenValue,
        int position,
        int estimatedWaitSec,
        QueueTokenStatus status
) {
    public static QueueTokenResponse from(QueueToken token) {
        return new QueueTokenResponse(
                token.getTokenValue(),
                token.getPosition(),
                token.getEstimatedWaitSec(),
                token.getStatus()
        );
    }
}
