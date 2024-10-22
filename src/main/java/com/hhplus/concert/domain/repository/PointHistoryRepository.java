package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.PointHistory;

import java.util.List;

public interface PointHistoryRepository {
    List<PointHistory> findByUserId(long id);
}
