package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.SeatRepository;
import com.hhplus.concert.infrastructure.persistence.JpaSeatRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRepositoryImpl implements SeatRepository {
    private final JpaSeatRepository jpaSeatRepository;

    public SeatRepositoryImpl(JpaSeatRepository jpaSeatRepository) {
        this.jpaSeatRepository = jpaSeatRepository;
    }

    @Override
    public List<Seat> findByConcertDetailIdAndStatus(Long concertDetailId, SeatStatus status) {
        return jpaSeatRepository.findByConcertDetailIdAndStatus(concertDetailId, status);
    }
}
