package com.hhplus.concert.user.infrastructure;

import com.hhplus.concert.user.domain.PointTransaction;
import com.hhplus.concert.user.domain.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PointTransactionRepositoryAdapter implements PointTransactionRepository {

    private final JpaPointTransactionRepository jpaPointTransactionRepository;

    @Override
    public PointTransaction save(PointTransaction transaction) {
        return jpaPointTransactionRepository.save(transaction);
    }

    @Override
    public List<PointTransaction> findByUserId(UUID userId) {
        return jpaPointTransactionRepository.findByUserId(userId);
    }
}
