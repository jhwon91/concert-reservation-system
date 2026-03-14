package com.hhplus.concert.user.domain.repository;

import com.hhplus.concert.user.domain.UserPoint;

import java.util.Optional;
import java.util.UUID;

public interface UserPointRepository {
    UserPoint save(UserPoint userPoint);
    Optional<UserPoint> findByUserId(UUID userId);
    Optional<UserPoint> findByUserIdWithLock(UUID userId);
}
