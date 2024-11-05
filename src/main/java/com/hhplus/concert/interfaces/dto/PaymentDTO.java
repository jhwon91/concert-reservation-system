package com.hhplus.concert.interfaces.dto;

import lombok.Builder;

public class PaymentDTO {

    @Builder
    public record PaymentResponseDTO(
            Long user_id,
            String user_name,
            Long point,
            String status
    ) {}

    @Builder
    public record PaymentRequestDTO(
            Long user_id,
            Long reservation_id
    ) {}


}
