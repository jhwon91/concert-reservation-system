package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.PointHistory;
import com.hhplus.concert.domain.repository.PointHistoryRepository;
import com.hhplus.concert.infrastructure.persistence.JpaPointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final JpaPointHistoryRepository jpaPointHistoryRepository;

    @Autowired
    public PointHistoryRepositoryImpl(JpaPointHistoryRepository jpaPointHistoryRepository) {
        this.jpaPointHistoryRepository = jpaPointHistoryRepository;
    }

    @Override
    public List<PointHistory> findByUserId(long id) {
        return jpaPointHistoryRepository.findByUserId(id);
    }
}
