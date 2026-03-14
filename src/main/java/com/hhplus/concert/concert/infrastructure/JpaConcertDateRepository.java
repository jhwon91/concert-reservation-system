package com.hhplus.concert.concert.infrastructure;

import com.hhplus.concert.concert.domain.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JpaConcertDateRepository extends JpaRepository<ConcertDate, UUID> {

    @Query("SELECT cd FROM ConcertDate cd WHERE cd.concertId = :concertId AND cd.date >= :now AND cd.availableSeats > 0")
    List<ConcertDate> findAvailableDates(@Param("concertId") UUID concertId, @Param("now") LocalDate now);
}
