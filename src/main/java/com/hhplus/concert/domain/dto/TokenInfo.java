package com.hhplus.concert.domain.dto;

import java.util.UUID;

public record TokenInfo(
        UUID token,
        String status,
        Integer position
) {
}
