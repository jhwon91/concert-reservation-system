package com.hhplus.concert.concert.infrastructure;

import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryAdapter implements SeatRepository {

    private final JpaSeatRepository jpaSeatRepository;

    @Override
    public Seat save(Seat seat) {
        return jpaSeatRepository.save(seat);
    }

    @Override
    public Optional<Seat> findById(UUID seatId) {
        return jpaSeatRepository.findById(seatId);
    }

    @Override
    public Optional<Seat> findByIdWithLock(UUID seatId) {
        return jpaSeatRepository.findByIdWithLock(seatId);
    }

    @Override
    public List<Seat> findByConcertDateIdAndStatus(UUID concertDateId, SeatStatus status) {
        return jpaSeatRepository.findByConcertDateIdAndStatus(concertDateId, status);
    }
}
