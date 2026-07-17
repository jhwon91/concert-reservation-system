package com.hhplus.concert.user.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.user.application.dto.GetUserPointQuery;
import com.hhplus.concert.user.application.dto.UserPointResult;
import com.hhplus.concert.user.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserPointService {

    private final UserPointRepository userPointRepository;

    @Transactional(readOnly = true)
    public UserPointResult getPoint(GetUserPointQuery query) {
        return userPointRepository.findByUserId(query.userId())
                .map(UserPointResult::from)
                .orElseThrow(() -> new CoreException(ErrorType.USER_POINT_NOT_FOUND, query.userId()));
    }
}
