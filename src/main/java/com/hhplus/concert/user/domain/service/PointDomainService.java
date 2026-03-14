package com.hhplus.concert.user.domain.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.domain.PointTransaction;
import com.hhplus.concert.user.domain.PointTransactionType;
import com.hhplus.concert.user.domain.UserPoint;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PointDomainService {

    private static final BigDecimal MAX_BALANCE = BigDecimal.valueOf(10_000_000);

    public PointTransaction charge(UserPoint userPoint, BigDecimal amount) {
        if (userPoint.getBalance().add(amount).compareTo(MAX_BALANCE) > 0) {
            throw new CoreException(ErrorType.MAX_BALANCE_EXCEEDED, userPoint.getBalance());
        }
        userPoint.charge(amount);
        return new PointTransaction(
                userPoint.getUserId(),
                amount,
                userPoint.getBalance(),
                PointTransactionType.CHARGE
        );
    }

    public PointTransaction use(UserPoint userPoint, BigDecimal amount, UUID relatedId) {
        userPoint.use(amount);
        return new PointTransaction(
                userPoint.getUserId(),
                amount,
                userPoint.getBalance(),
                PointTransactionType.USE,
                relatedId
        );
    }
}
