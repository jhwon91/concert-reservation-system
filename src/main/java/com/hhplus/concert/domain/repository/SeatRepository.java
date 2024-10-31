package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status);
    Optional<Seat> findById(long seatId);
    Seat save(Seat seat);
}
