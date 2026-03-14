package com.hhplus.concert.reservation.domain.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.ReservationStatus;
import org.springframework.stereotype.Service;

@Service
public class ReservationDomainService {

    public void validatePending(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CoreException(ErrorType.RESERVATION_NOT_PENDING, reservation.getStatus());
        }
        if (reservation.isExpired()) {
            throw new CoreException(ErrorType.RESERVATION_EXPIRED, reservation.getExpiresAt());
        }
    }
}
