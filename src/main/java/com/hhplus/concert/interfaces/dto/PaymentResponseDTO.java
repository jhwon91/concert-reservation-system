package com.hhplus.concert.interfaces.dto;

import lombok.Builder;

@Builder
public record PaymentResponseDTO(
        Long user_id,
        String user_name,
        Long point,
        String status
) {}