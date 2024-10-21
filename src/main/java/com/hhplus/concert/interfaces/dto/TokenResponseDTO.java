package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.domain.dto.TokenInfo;
import java.util.UUID;

public record TokenResponseDTO(
        String token,
        String status,
        Integer position
) { }
