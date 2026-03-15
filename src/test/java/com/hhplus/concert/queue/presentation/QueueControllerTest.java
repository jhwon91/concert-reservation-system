package com.hhplus.concert.queue.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.application.dto.IssueQueueTokenCommand;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.application.service.GetQueueStatusService;
import com.hhplus.concert.queue.application.service.IssueQueueTokenService;
import com.hhplus.concert.queue.domain.QueueTokenStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.presentation.dto.IssueTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IssueQueueTokenService issueQueueTokenService;

    @MockitoBean
    private GetQueueStatusService getQueueStatusService;

    @MockitoBean
    private QueueTokenRepository queueTokenRepository;

    @Test
    @DisplayName("토큰 발급 성공 시 200과 토큰 정보를 반환한다")
    void 토큰_발급_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        QueueTokenResponse response = new QueueTokenResponse("test-token", 1, 10, QueueTokenStatus.WAITING);

        given(issueQueueTokenService.issue(any(IssueQueueTokenCommand.class))).willReturn(response);

        IssueTokenRequest request = new IssueTokenRequest(userId);

        // when & then
        mockMvc.perform(post("/api/v1/queue/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenValue").value("test-token"))
                .andExpect(jsonPath("$.data.status").value("WAITING"))
                .andExpect(jsonPath("$.data.position").value(1));
    }

    @Test
    @DisplayName("존재하지 않는 유저 토큰 발급 시 404를 반환한다")
    void 없는_유저_토큰_발급_시_404() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(issueQueueTokenService.issue(any(IssueQueueTokenCommand.class)))
                .willThrow(new CoreException(ErrorType.USER_NOT_FOUND, userId));

        IssueTokenRequest request = new IssueTokenRequest(userId);

        // when & then
        mockMvc.perform(post("/api/v1/queue/tokens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("대기 순번 조회 성공 시 200과 토큰 상태를 반환한다")
    void 대기_순번_조회_성공() throws Exception {
        // given
        String tokenValue = "test-token";
        QueueTokenResponse response = new QueueTokenResponse(tokenValue, 3, 30, QueueTokenStatus.WAITING);

        given(getQueueStatusService.getStatus(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/queue/status")
                        .param("token", tokenValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenValue").value(tokenValue))
                .andExpect(jsonPath("$.data.position").value(3))
                .andExpect(jsonPath("$.data.status").value("WAITING"));
    }

    @Test
    @DisplayName("존재하지 않는 토큰 순번 조회 시 404를 반환한다")
    void 없는_토큰_순번_조회_시_404() throws Exception {
        // given
        String tokenValue = "non-existent-token";
        given(getQueueStatusService.getStatus(any()))
                .willThrow(new CoreException(ErrorType.TOKEN_NOT_FOUND, tokenValue));

        // when & then
        mockMvc.perform(get("/api/v1/queue/status")
                        .param("token", tokenValue))
                .andExpect(status().isNotFound());
    }
}
