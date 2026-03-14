package com.hhplus.concert.common.interceptor;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class QueueTokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Queue-Token";

    private final QueueTokenRepository queueTokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String tokenValue = request.getHeader(TOKEN_HEADER);

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new CoreException(ErrorType.TOKEN_MISSING);
        }

        QueueToken token = queueTokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, tokenValue));

        if (!token.isActive()) {
            throw new CoreException(ErrorType.TOKEN_NOT_ACTIVE, tokenValue);
        }

        return true;
    }
}
