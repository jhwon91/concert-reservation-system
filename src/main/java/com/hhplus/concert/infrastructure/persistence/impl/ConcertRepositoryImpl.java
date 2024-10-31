package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.repository.ConcertRepository;
import com.hhplus.concert.infrastructure.persistence.JpaConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {
    private final JpaConcertRepository jpaConcertRepository;

    @Autowired
    public ConcertRepositoryImpl(JpaConcertRepository jpaConcertRepository) {
        this.jpaConcertRepository = jpaConcertRepository;
    }

    @Override
    public Optional<Concert> findById(long id) {
        return jpaConcertRepository.findById(id);
    }

    @Override
    public boolean exists(long id) {
        return jpaConcertRepository.existsById(id);
    }

    @Override
    @Transactional
    public Concert save(Concert concert) {
        return jpaConcertRepository.save(concert);
    }
}
