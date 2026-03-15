package com.hhplus.concert.payment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.payment.application.dto.PaymentResponse;
import com.hhplus.concert.payment.application.service.ProcessPaymentService;
import com.hhplus.concert.payment.domain.PaymentMethod;
import com.hhplus.concert.payment.domain.PaymentStatus;
import com.hhplus.concert.payment.presentation.dto.ProcessPaymentRequest;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProcessPaymentService processPaymentService;

    @MockitoBean
    private QueueTokenRepository queueTokenRepository;

    private static final String VALID_TOKEN = "valid-token";
    private static final String TOKEN_HEADER = "X-Queue-Token";

    @BeforeEach
    void setUp() {
        QueueToken activeToken = new QueueToken(UUID.randomUUID(), TokenValue.of(VALID_TOKEN), 1, 10);
        activeToken.activate();
        given(queueTokenRepository.findByTokenValue(VALID_TOKEN)).willReturn(Optional.of(activeToken));
    }

    @Test
    @DisplayName("결제 성공 시 200과 결제 정보를 반환한다")
    void 결제_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        PaymentResponse response = new PaymentResponse(
                paymentId, BigDecimal.valueOf(99_000), PaymentStatus.COMPLETED, LocalDateTime.now());

        given(processPaymentService.process(any())).willReturn(response);

        ProcessPaymentRequest request = new ProcessPaymentRequest(userId, reservationId, PaymentMethod.POINT);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("토큰 없이 결제 시 401을 반환한다")
    void 토큰_없이_결제_시_401() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        ProcessPaymentRequest request = new ProcessPaymentRequest(userId, reservationId, PaymentMethod.POINT);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 예약 결제 시 404를 반환한다")
    void 없는_예약_결제_시_404() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        given(processPaymentService.process(any()))
                .willThrow(new CoreException(ErrorType.RESERVATION_NOT_FOUND, reservationId));

        ProcessPaymentRequest request = new ProcessPaymentRequest(userId, reservationId, PaymentMethod.POINT);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잔액 부족 결제 시 400을 반환한다")
    void 잔액_부족_결제_시_400() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        given(processPaymentService.process(any()))
                .willThrow(new CoreException(ErrorType.INSUFFICIENT_BALANCE));

        ProcessPaymentRequest request = new ProcessPaymentRequest(userId, reservationId, PaymentMethod.POINT);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
