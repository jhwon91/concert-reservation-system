package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;

import java.util.List;

public interface SeatRepository {
    List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status);
    Seat findByIdWithLock(long seatId);
    Seat save(Seat seat);
}
