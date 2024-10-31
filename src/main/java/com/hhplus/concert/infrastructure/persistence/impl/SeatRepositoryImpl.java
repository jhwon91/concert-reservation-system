package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.SeatRepository;
import com.hhplus.concert.infrastructure.persistence.JpaSeatRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Seat> findById(long seatId) {
        return jpaSeatRepository.findById(seatId);
    }

    @Override
    @Transactional
    public Seat save(Seat seat) {
        return jpaSeatRepository.save(seat);
    }
}
