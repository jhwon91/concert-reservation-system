package com.hhplus.concert.concert.infrastructure;

import com.hhplus.concert.concert.domain.Concert;
import com.hhplus.concert.concert.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryAdapter implements ConcertRepository {

    private final JpaConcertRepository jpaConcertRepository;

    @Override
    public Concert save(Concert concert) {
        return jpaConcertRepository.save(concert);
    }

    @Override
    public Optional<Concert> findById(UUID concertId) {
        return jpaConcertRepository.findById(concertId);
    }
}
