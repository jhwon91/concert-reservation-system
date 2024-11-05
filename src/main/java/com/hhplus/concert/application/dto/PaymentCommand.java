package com.hhplus.concert.application.dto;

import lombok.Builder;

import java.util.UUID;

public class PaymentCommand {
    @Builder
    public record payment(
            UUID token,
            Long userId,
            Long reservationId
    ){}
}
