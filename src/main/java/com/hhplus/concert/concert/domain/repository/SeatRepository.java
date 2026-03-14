package com.hhplus.concert.concert.domain.repository;

import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository {
    Seat save(Seat seat);
    Optional<Seat> findById(UUID seatId);
    Optional<Seat> findByIdWithLock(UUID seatId);
    List<Seat> findByConcertDateIdAndStatus(UUID concertDateId, SeatStatus status);
}
