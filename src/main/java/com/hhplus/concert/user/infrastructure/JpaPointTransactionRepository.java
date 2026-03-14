package com.hhplus.concert.user.infrastructure;

import com.hhplus.concert.user.domain.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPointTransactionRepository extends JpaRepository<PointTransaction, UUID> {

    List<PointTransaction> findByUserId(UUID userId);
}
