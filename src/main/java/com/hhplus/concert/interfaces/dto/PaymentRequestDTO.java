package com.hhplus.concert.interfaces.dto;

public record PaymentRequestDTO(
        Long user_id,
        Long reservation_id
) {}