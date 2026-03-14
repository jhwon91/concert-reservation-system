package com.hhplus.concert.queue.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.application.dto.IssueQueueTokenCommand;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.service.QueueDomainService;
import com.hhplus.concert.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueQueueTokenService {

    private final UserRepository userRepository;
    private final QueueTokenRepository queueTokenRepository;
    private final QueueDomainService queueDomainService;

    @Transactional
    public QueueTokenResponse issue(IssueQueueTokenCommand command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, command.userId()));

        long currentQueueSize = queueTokenRepository.countByStatus(QueueTokenStatus.WAITING);
        QueueToken token = queueDomainService.issueToken(command.userId(), (int) currentQueueSize);

        return QueueTokenResponse.from(queueTokenRepository.save(token));
    }
}
