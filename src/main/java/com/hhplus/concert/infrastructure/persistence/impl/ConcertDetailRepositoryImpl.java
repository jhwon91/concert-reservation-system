package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.ConcertDetailRepository;
import com.hhplus.concert.infrastructure.persistence.JpaConcertDetailRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertDetailRepositoryImpl implements ConcertDetailRepository {
    private final JpaConcertDetailRepository jpaConcertDetailRepository;

    public ConcertDetailRepositoryImpl(JpaConcertDetailRepository jpaConcertDetailRepository) {
        this.jpaConcertDetailRepository = jpaConcertDetailRepository;
    }

    @Override
    public List<ConcertDetails> findAvailableConcertDatesByConcertId(long concertId, SeatStatus status) {
        return jpaConcertDetailRepository.findAvailableConcertDatesByConcertId(concertId, status);
    }
}
