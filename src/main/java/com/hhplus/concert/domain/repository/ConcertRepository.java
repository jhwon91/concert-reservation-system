package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Concert;

import java.util.Optional;

public interface ConcertRepository {
    Optional<Concert> findById(long id);
    boolean exists(long id);
}
