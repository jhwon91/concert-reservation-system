package com.hhplus.concert.user.infrastructure;

import com.hhplus.concert.user.domain.UserPoint;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserPointRepository extends JpaRepository<UserPoint, UUID> {

    Optional<UserPoint> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT up FROM UserPoint up WHERE up.userId = :userId")
    Optional<UserPoint> findByUserIdWithLock(@Param("userId") UUID userId);
}
