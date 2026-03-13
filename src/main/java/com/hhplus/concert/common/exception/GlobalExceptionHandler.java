package com.hhplus.concert.common.exception;

import com.hhplus.concert.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<CommonResponse<ErrorResponse>> handleCoreException(CoreException e) {
        switch (e.getErrorType().getLevel()) {
            case ERROR -> log.error("Error occurred: ", e);
            case WARN -> log.warn("Warning: {}", e.getMessage());
            default -> log.info("Info: {}", e.getMessage());
        }

        HttpStatus status = switch (e.getErrorType().getErrorCode()) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CONFLICT -> HttpStatus.CONFLICT;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorType(e.getErrorType().name())
                .payload(e.getPayload())
                .build();

        return ResponseEntity.status(status)
                .body(CommonResponse.error(errorResponse, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<ErrorResponse>> handleException(Exception e) {
        log.error("Unexpected error occurred: ", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorType("INTERNAL_ERROR")
                .payload(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(errorResponse, "서버 내부 오류가 발생했습니다."));
    }
}
