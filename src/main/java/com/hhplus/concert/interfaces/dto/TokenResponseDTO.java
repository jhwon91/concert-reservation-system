package com.hhplus.concert.interfaces.dto;

public record TokenResponseDTO(
        String waiting_id,
        String status,
        Integer position
) {}
