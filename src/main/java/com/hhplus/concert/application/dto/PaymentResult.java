package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;

public class PaymentResult {
    @Builder
    public record Payment(
            Long user_id,
            String user_name,
            Long point,
            SeatStatus status
    ) {}
}
