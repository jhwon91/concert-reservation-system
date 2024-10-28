package com.hhplus.concert.domain.support.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;

@Getter
@AllArgsConstructor
public enum ErrorType {
    USER_NOT_FOUND(ErrorCode.NOT_FOUND,"사용자를 찾을 수 없습니다.",LogLevel.WARN),
    CONCERT_DETAIL_NOT_FOUND(ErrorCode.NOT_FOUND, "콘서트 일정을 찾을 수 없습니다.",LogLevel.WARN),
    CONCERT_NOT_FOUND(ErrorCode.NOT_FOUND, "콘서트 일정을 찾을 수 없습니다.",LogLevel.WARN),
    TOKEN_NOT_FOUND(ErrorCode.NOT_FOUND, "해당 토큰을 찾을 수 없습니다.", LogLevel.WARN),
    RESERVATION_NOT_FOUND(ErrorCode.NOT_FOUND, "예약이 존재하지 않습니다.", LogLevel.WARN),

    TOKEN_NOT_ACTIVE(ErrorCode.UNAUTHORIZED, "활성 상태의 토큰이 아닙니다.", LogLevel.INFO),
    USER_NOT_MATCHED_TOKEN(ErrorCode.UNAUTHORIZED, "해당 토큰의 유저가 아닙니다.", LogLevel.INFO),

    PAYMENT_TIME_EXPIRED(ErrorCode.BUSINESS_ERROR,"결제 가능 시간이 지났습니다.",LogLevel.INFO),
    SEAT_NOT_RESERVABLE(ErrorCode.BUSINESS_ERROR,"좌석 예약가능한 상태가 아닙니다.",LogLevel.INFO),
    SEAT_NOT_PAYABLE(ErrorCode.BUSINESS_ERROR,"좌석 결제가능한 상태가 아닙니다.",LogLevel.INFO),

    INVALID_CHARGE_AMOUNT(ErrorCode.BUSINESS_ERROR,"충전액이 0보다 작습니다.",LogLevel.INFO),
    INSUFFICIENT_USER_BALANCE(ErrorCode.BUSINESS_ERROR,"사용자 잔액이 부족합니다.",LogLevel.INFO),
    INVALID_CONCERT_PRICE(ErrorCode.BUSINESS_ERROR,"콘서트 가격이 0보다 작습니다.",LogLevel.INFO)
    ;

    private final ErrorCode errorCode;
    private final String message;
    private final LogLevel level;

}
