package com.hhplus.concert.user.infrastructure;

import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryAdapter implements UserPointRepository {

    private final JpaUserPointRepository jpaUserPointRepository;

    @Override
    public UserPoint save(UserPoint userPoint) {
        return jpaUserPointRepository.save(userPoint);
    }

    @Override
    public Optional<UserPoint> findByUserId(UUID userId) {
        return jpaUserPointRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserPoint> findByUserIdWithLock(UUID userId) {
        return jpaUserPointRepository.findByUserIdWithLock(userId);
    }
}
