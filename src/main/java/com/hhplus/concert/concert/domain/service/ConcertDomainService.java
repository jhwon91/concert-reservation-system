package com.hhplus.concert.concert.domain.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;
import org.springframework.stereotype.Service;

@Service
public class ConcertDomainService {

    public void validateSeatAvailable(Seat seat) {
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new CoreException(ErrorType.SEAT_NOT_AVAILABLE, seat.getStatus());
        }
    }
}
