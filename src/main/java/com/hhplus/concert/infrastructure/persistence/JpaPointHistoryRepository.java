package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserId(long userId);
}
