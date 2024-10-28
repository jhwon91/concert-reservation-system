package com.hhplus.concert.interfaces.support.error;

import com.hhplus.concert.domain.support.error.CoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CoreExceptionHandler {
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handle(CoreException e) {
        // 로그 처리
        switch (e.getErrorType().getLevel()) {
            case ERROR -> log.error("Error occurred: ", e);
            case WARN -> log.warn("Warning: ", e);
            default -> log.info("Info: ", e);
        }

        // HTTP 상태 코드 매핑
        HttpStatus status;
        switch (e.getErrorType().getErrorCode()) {
            case DB_ERROR, BUSINESS_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
            case CLIENT_ERROR, UNAUTHORIZED -> status = HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
            default -> status = HttpStatus.OK; // 200으로 표현하는 에러 케이스
        }

        // 에러 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(status.value(), e.getMessage(),e.getErrorType().toString(), e.getPayload());

        // ResponseEntity로 에러 응답 반환
        return new ResponseEntity<>(errorResponse, status);
    }
}
