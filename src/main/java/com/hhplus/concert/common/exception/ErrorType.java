package com.hhplus.concert.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // 400 Bad Request
    INVALID_CHARGE_AMOUNT(ErrorCode.BAD_REQUEST, "충전 금액은 1,000원 이상이어야 합니다.", LogLevel.INFO),
    CHARGE_AMOUNT_EXCEEDED(ErrorCode.BAD_REQUEST, "1회 최대 충전 금액은 1,000,000원입니다.", LogLevel.INFO),
    MAX_BALANCE_EXCEEDED(ErrorCode.BAD_REQUEST, "최대 보유 잔액은 10,000,000원입니다.", LogLevel.INFO),
    INVALID_SEAT_NUMBER(ErrorCode.BAD_REQUEST, "좌석 번호는 1~50 사이여야 합니다.", LogLevel.INFO),
    INVALID_MONEY_AMOUNT(ErrorCode.BAD_REQUEST, "금액은 0 이상이어야 합니다.", LogLevel.INFO),
    INSUFFICIENT_BALANCE(ErrorCode.BAD_REQUEST, "잔액이 부족합니다.", LogLevel.INFO),

    // 401 Unauthorized
    TOKEN_NOT_ACTIVE(ErrorCode.UNAUTHORIZED, "활성 상태의 토큰이 아닙니다.", LogLevel.INFO),
    TOKEN_EXPIRED(ErrorCode.UNAUTHORIZED, "만료된 토큰입니다.", LogLevel.INFO),
    TOKEN_MISSING(ErrorCode.UNAUTHORIZED, "토큰이 누락되었습니다.", LogLevel.INFO),

    // 404 Not Found
    USER_NOT_FOUND(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.", LogLevel.INFO),
    USER_POINT_NOT_FOUND(ErrorCode.NOT_FOUND, "사용자 포인트를 찾을 수 없습니다.", LogLevel.INFO),
    CONCERT_NOT_FOUND(ErrorCode.NOT_FOUND, "콘서트를 찾을 수 없습니다.", LogLevel.INFO),
    CONCERT_DATE_NOT_FOUND(ErrorCode.NOT_FOUND, "콘서트 일정을 찾을 수 없습니다.", LogLevel.INFO),
    SEAT_NOT_FOUND(ErrorCode.NOT_FOUND, "좌석이 존재하지 않습니다.", LogLevel.INFO),
    RESERVATION_NOT_FOUND(ErrorCode.NOT_FOUND, "예약이 존재하지 않습니다.", LogLevel.INFO),
    TOKEN_NOT_FOUND(ErrorCode.NOT_FOUND, "해당 토큰을 찾을 수 없습니다.", LogLevel.INFO),
    PAYMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "결제를 찾을 수 없습니다.", LogLevel.INFO),

    // 409 Conflict
    SEAT_NOT_AVAILABLE(ErrorCode.CONFLICT, "예약 가능한 좌석이 아닙니다.", LogLevel.INFO),
    SEAT_NOT_RESERVED(ErrorCode.CONFLICT, "결제 가능한 좌석 상태가 아닙니다.", LogLevel.INFO),
    RESERVATION_EXPIRED(ErrorCode.CONFLICT, "예약 시간이 만료되었습니다.", LogLevel.INFO),
    RESERVATION_NOT_PENDING(ErrorCode.CONFLICT, "결제 대기 중인 예약이 아닙니다.", LogLevel.INFO),
    RESERVATION_ALREADY_CANCELLED(ErrorCode.CONFLICT, "이미 취소된 예약입니다.", LogLevel.INFO),
    PAYMENT_ALREADY_COMPLETED(ErrorCode.CONFLICT, "이미 완료된 결제입니다.", LogLevel.INFO),
    QUEUE_TOKEN_INVALID_STATUS(ErrorCode.CONFLICT, "유효하지 않은 토큰 상태입니다.", LogLevel.INFO),
    ;

    private final ErrorCode errorCode;
    private final String message;
    private final LogLevel level;
}
