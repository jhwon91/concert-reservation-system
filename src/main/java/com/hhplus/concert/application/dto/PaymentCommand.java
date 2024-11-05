package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class PaymentCommand {
    @Builder
    public record PaymentConcert(
            UUID token,
            Long userId,
            Long reservationId
    ){}
}
