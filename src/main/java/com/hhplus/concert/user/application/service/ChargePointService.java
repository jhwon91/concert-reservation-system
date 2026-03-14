package com.hhplus.concert.user.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.application.dto.ChargePointCommand;
import com.hhplus.concert.user.application.dto.UserPointResponse;
import com.hhplus.concert.user.domain.PointTransaction;
import com.hhplus.concert.user.domain.UserPoint;
import com.hhplus.concert.user.domain.repository.PointTransactionRepository;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
import com.hhplus.concert.user.domain.service.PointDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargePointService {

    private final UserPointRepository userPointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final PointDomainService pointDomainService;

    @Transactional
    public UserPointResponse charge(ChargePointCommand command) {
        UserPoint userPoint = userPointRepository.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.USER_POINT_NOT_FOUND, command.userId()));

        PointTransaction transaction = pointDomainService.charge(userPoint, command.amount());

        userPointRepository.save(userPoint);
        pointTransactionRepository.save(transaction);

        return UserPointResponse.from(userPoint);
    }
}
