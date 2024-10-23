package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status);
}
