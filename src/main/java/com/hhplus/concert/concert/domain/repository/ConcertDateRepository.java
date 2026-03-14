package com.hhplus.concert.concert.domain.repository;

import com.hhplus.concert.concert.domain.ConcertDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcertDateRepository {
    ConcertDate save(ConcertDate concertDate);
    Optional<ConcertDate> findById(UUID concertDateId);
    List<ConcertDate> findAvailableDates(UUID concertId, LocalDate now);
}
