package com.hhplus.concert.interfaces.dto;

public record UserBalanceResponseDTO(
        Long user_id,
        String user_name,
        Long point
) {}