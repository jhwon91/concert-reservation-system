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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM Seat cs WHERE cs.id = :seatId")
    Seat findByIdWithLock(@Param("seatId") Long seatId);
    List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status);
}
