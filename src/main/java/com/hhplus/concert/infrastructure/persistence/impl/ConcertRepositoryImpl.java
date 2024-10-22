package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.repository.ConcertRepository;
import com.hhplus.concert.infrastructure.persistence.JpaConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {
    private final JpaConcertRepository jpaConcertRepository;

    @Autowired
    public ConcertRepositoryImpl(JpaConcertRepository jpaConcertRepository) {
        this.jpaConcertRepository = jpaConcertRepository;
    }

    @Override
    public Concert findById(long id) {
        return jpaConcertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("콘서트가 존재하지 않습니다."));
    }

    @Override
    public boolean exists(long id) {
        return jpaConcertRepository.existsById(id);
    }
}
