package com.hhplus.concert.concert.infrastructure;

import com.hhplus.concert.concert.domain.ConcertDate;
import com.hhplus.concert.concert.domain.repository.ConcertDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConcertDateRepositoryAdapter implements ConcertDateRepository {

    private final JpaConcertDateRepository jpaConcertDateRepository;

    @Override
    public ConcertDate save(ConcertDate concertDate) {
        return jpaConcertDateRepository.save(concertDate);
    }

    @Override
    public Optional<ConcertDate> findById(UUID concertDateId) {
        return jpaConcertDateRepository.findById(concertDateId);
    }

    @Override
    public List<ConcertDate> findAvailableDates(UUID concertId, LocalDate now) {
        return jpaConcertDateRepository.findAvailableDates(concertId, now);
    }
}
