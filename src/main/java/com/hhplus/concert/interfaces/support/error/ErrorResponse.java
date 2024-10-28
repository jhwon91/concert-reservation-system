package com.hhplus.concert.interfaces.support.error;

import com.hhplus.concert.domain.support.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.logging.LogLevel;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private String errorType;
    private Object payload;
}
