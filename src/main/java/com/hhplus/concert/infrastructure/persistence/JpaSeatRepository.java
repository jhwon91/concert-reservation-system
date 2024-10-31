package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaSeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status);
}
