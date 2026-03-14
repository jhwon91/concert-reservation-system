package com.hhplus.concert.user.domain.repository;

import com.hhplus.concert.user.domain.PointTransaction;

import java.util.List;
import java.util.UUID;

public interface PointTransactionRepository {
    PointTransaction save(PointTransaction transaction);
    List<PointTransaction> findByUserId(UUID userId);
}
