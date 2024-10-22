package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Concert;

public interface ConcertRepository {
    Concert findById(long id);
    boolean exists(long id);
}
