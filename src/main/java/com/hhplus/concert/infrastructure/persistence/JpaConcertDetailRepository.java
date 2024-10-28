package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaConcertDetailRepository extends JpaRepository<ConcertDetails, Long> {
    @Query("SELECT DISTINCT cd FROM ConcertDetail cd JOIN Seat s ON cd.id = s.concertDetailId WHERE cd.concertId = :concertId AND s.status = :status")
    List<ConcertDetails> findAvailableConcertDatesByConcertId(@Param("concertId") Long concertId, @Param("status") SeatStatus status);
}
