package com.hhplus.concert.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.user.application.dto.UserPointResult;
import com.hhplus.concert.user.application.service.ChargePointService;
import com.hhplus.concert.user.application.service.GetUserPointService;
import com.hhplus.concert.user.presentation.dto.ChargePointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetUserPointService getUserPointService;

    @MockitoBean
    private ChargePointService chargePointService;

    @MockitoBean
    private QueueTokenRepository queueTokenRepository;

    @Test
    @DisplayName("잔액 조회 성공 시 200과 잔액 정보를 반환한다")
    void 잔액_조회_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserPointResult result = new UserPointResult(userId, BigDecimal.valueOf(50_000));

        given(getUserPointService.getPoint(any())).willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/point", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.balance").value(50_000));
    }

    @Test
    @DisplayName("존재하지 않는 유저 잔액 조회 시 404를 반환한다")
    void 없는_유저_잔액_조회_시_404() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(getUserPointService.getPoint(any()))
                .willThrow(new CoreException(ErrorType.USER_NOT_FOUND, userId));

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/point", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잔액 충전 성공 시 200과 충전된 잔액 정보를 반환한다")
    void 잔액_충전_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        BigDecimal chargedBalance = BigDecimal.valueOf(10_000);
        UserPointResult result = new UserPointResult(userId, chargedBalance);

        given(chargePointService.charge(any())).willReturn(result);

        ChargePointRequest request = new ChargePointRequest(BigDecimal.valueOf(10_000));

        // when & then
        mockMvc.perform(patch("/api/v1/users/{userId}/point", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.balance").value(10_000));
    }

    @Test
    @DisplayName("유효하지 않은 충전 금액 시 400을 반환한다")
    void 잔액_부족_충전_시_400() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(chargePointService.charge(any()))
                .willThrow(new CoreException(ErrorType.INVALID_CHARGE_AMOUNT));

        ChargePointRequest request = new ChargePointRequest(BigDecimal.valueOf(500));

        // when & then
        mockMvc.perform(patch("/api/v1/users/{userId}/point", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
