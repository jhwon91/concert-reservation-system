package com.hhplus.concert.user.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ChargePointCommand(UUID userId, BigDecimal amount) {
}
