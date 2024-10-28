package com.hhplus.concert.domain.enums;

/**
 * 좌석 상태 종류
 * - AVAILABLE : 예약 가능
 * - TEMPORARY_ALLOCATED : 임시 할당 상태
 * - RESERVED : 예약 완료
 */
public enum SeatStatus {
    AVAILABLE,
    TEMPORARY_ALLOCATED,
    RESERVED
}
