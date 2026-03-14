package com.hhplus.concert.concert.domain.repository;

import com.hhplus.concert.concert.domain.Concert;

import java.util.Optional;
import java.util.UUID;

public interface ConcertRepository {
    Concert save(Concert concert);
    Optional<Concert> findById(UUID concertId);
}
