package com.hhplus.concert.reservation.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.queue.domain.vo.TokenValue;
import com.hhplus.concert.reservation.application.dto.ReservationResponse;
import com.hhplus.concert.reservation.application.service.CreateReservationService;
import com.hhplus.concert.reservation.domain.ReservationStatus;
import com.hhplus.concert.reservation.presentation.dto.CreateReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateReservationService createReservationService;

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
    @DisplayName("좌석 예약 성공 시 200과 예약 정보를 반환한다")
    void 좌석_예약_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        ReservationResponse response = new ReservationResponse(
                reservationId, seatId, ReservationStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        given(createReservationService.create(any())).willReturn(response);

        CreateReservationRequest request = new CreateReservationRequest(userId, seatId);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservationId").value(reservationId.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("토큰 없이 예약 시 401을 반환한다")
    void 토큰_없이_예약_시_401() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        CreateReservationRequest request = new CreateReservationRequest(userId, seatId);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 좌석 예약 시 404를 반환한다")
    void 없는_좌석_예약_시_404() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        given(createReservationService.create(any()))
                .willThrow(new CoreException(ErrorType.SEAT_NOT_FOUND, seatId));

        CreateReservationRequest request = new CreateReservationRequest(userId, seatId);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이미 예약된 좌석 예약 시 409를 반환한다")
    void 이미_예약된_좌석_예약_시_409() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        given(createReservationService.create(any()))
                .willThrow(new CoreException(ErrorType.SEAT_NOT_AVAILABLE, seatId));

        CreateReservationRequest request = new CreateReservationRequest(userId, seatId);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .header(TOKEN_HEADER, VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
