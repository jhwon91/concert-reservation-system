package com.hhplus.concert.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final String errorType;
    private final Object payload;
}
