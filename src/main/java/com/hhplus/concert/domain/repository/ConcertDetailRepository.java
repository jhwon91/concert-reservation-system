package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;

import java.util.List;
import java.util.Optional;

public interface ConcertDetailRepository {
    List<ConcertDetails> findAvailableConcertDatesByConcertId(long concertId, SeatStatus status);
    boolean exists(long id);
    Optional<ConcertDetails> findById(long id);
}
