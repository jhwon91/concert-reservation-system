package com.hhplus.concert.queue.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.application.dto.GetQueueStatusQuery;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetQueueStatusService {

    private final QueueTokenRepository queueTokenRepository;

    @Transactional(readOnly = true)
    public QueueTokenResponse getStatus(GetQueueStatusQuery query) {
        return queueTokenRepository.findByTokenValue(query.tokenValue())
                .map(QueueTokenResponse::from)
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, query.tokenValue()));
    }
}
