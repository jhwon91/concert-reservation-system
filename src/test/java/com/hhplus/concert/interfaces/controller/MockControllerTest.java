package com.hhplus.concert.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.interfaces.dto.BalanceChargeRequestDTO;
import com.hhplus.concert.interfaces.dto.PaymentRequestDTO;
import com.hhplus.concert.interfaces.dto.ReservationRequestDTO;
import com.hhplus.concert.interfaces.dto.TokenRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockController.class)
class MockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 토큰_발급() throws Exception {
//        TokenRequestDTO request = new TokenRequestDTO(1L);
//
//        mockMvc.perform(post("/mock/api/tokens")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.waiting_id").exists())
//                .andExpect(jsonPath("$.status").value("WAIT"))
//                .andExpect(jsonPath("$.position").value(5));
    }

    @Test
    void 대기열_상태_확인() throws Exception {
        mockMvc.perform(get("/mock/api/tokens/status")
                        .header("Authorization", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAIT"))
                .andExpect(jsonPath("$.position").value(5));
    }

    @Test
    void 예약_가능한_날짜_조회() throws Exception {
        mockMvc.perform(get("/mock/api/reservations/dates/{concert_id}", 1L)
                        .header("Authorization", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concert_id").value(1))
                .andExpect(jsonPath("$.concert_title").value("콘서트1"))
                .andExpect(jsonPath("$.concert_detail").isArray());
    }

    @Test
    void 특정_날짜의_예약_가능한_좌석_조회() throws Exception {
        mockMvc.perform(get("/mock/api/reservations/seats")
                        .param("concert_detail_id", "1")
                        .param("concert_date", "2024-10-01")
                        .header("Authorization", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concert_id").value(1))
                .andExpect(jsonPath("$.seats").isArray());
    }

    @Test
    void 좌석_예약() throws Exception {
        ReservationRequestDTO request = new ReservationRequestDTO(1L, 1L, 1L, "2024-10-01");

        mockMvc.perform(post("/mock/api/reservations")
                        .header("Authorization", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservation_id").value(1))
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.status").value("TEMPORARY_ALLOCATED"));
    }

    @Test
    void 사용자_잔액_조회() throws Exception {
        mockMvc.perform(get("/mock/api/users/balance/{user_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.point").value(100));
    }

    @Test
    void 잔액_충전() throws Exception{
        BalanceChargeRequestDTO request = new BalanceChargeRequestDTO(1000L);

        mockMvc.perform(post("/mock/api/users/balance/{user_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.point").value(2000));
    }

    @Test
    void 결제_진행() throws Exception{
        PaymentRequestDTO request = new PaymentRequestDTO(1L, 1L);

        mockMvc.perform(post("/mock/api/payments")
                        .header("Authorization", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}