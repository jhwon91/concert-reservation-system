package com.hhplus.concert.concert.presentation;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.application.dto.ConcertDateResponse;
import com.hhplus.concert.concert.application.dto.SeatResponse;
import com.hhplus.concert.concert.application.service.GetAvailableDatesService;
import com.hhplus.concert.concert.application.service.GetAvailableSeatsService;
import com.hhplus.concert.concert.domain.SeatStatus;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAvailableDatesService getAvailableDatesService;

    @MockitoBean
    private GetAvailableSeatsService getAvailableSeatsService;

    @MockitoBean
    private QueueTokenRepository queueTokenRepository;

    @Test
    @DisplayName("예약 가능 날짜 조회 성공 시 200과 날짜 목록을 반환한다")
    void 예약_가능_날짜_조회_성공() throws Exception {
        // given
        UUID concertId = UUID.randomUUID();
        UUID concertDateId = UUID.randomUUID();
        List<ConcertDateResponse> response = List.of(
                new ConcertDateResponse(concertDateId, LocalDate.of(2026, 4, 1), 50)
        );

        given(getAvailableDatesService.getDates(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/concerts/{concertId}/dates", concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].concertDateId").value(concertDateId.toString()))
                .andExpect(jsonPath("$.data[0].availableSeats").value(50));
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 날짜 조회 시 404를 반환한다")
    void 없는_콘서트_날짜_조회_시_404() throws Exception {
        // given
        UUID concertId = UUID.randomUUID();
        given(getAvailableDatesService.getDates(any()))
                .willThrow(new CoreException(ErrorType.CONCERT_NOT_FOUND, concertId));

        // when & then
        mockMvc.perform(get("/api/v1/concerts/{concertId}/dates", concertId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("예약 가능 좌석 조회 성공 시 200과 좌석 목록을 반환한다")
    void 예약_가능_좌석_조회_성공() throws Exception {
        // given
        UUID concertDateId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        List<SeatResponse> response = List.of(
                new SeatResponse(seatId, 1, BigDecimal.valueOf(99_000), SeatStatus.AVAILABLE)
        );

        given(getAvailableSeatsService.getSeats(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/concerts/dates/{concertDateId}/seats", concertDateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].seatId").value(seatId.toString()))
                .andExpect(jsonPath("$.data[0].seatNumber").value(1))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("존재하지 않는 날짜 좌석 조회 시 404를 반환한다")
    void 없는_날짜_좌석_조회_시_404() throws Exception {
        // given
        UUID concertDateId = UUID.randomUUID();
        given(getAvailableSeatsService.getSeats(any()))
                .willThrow(new CoreException(ErrorType.CONCERT_DATE_NOT_FOUND, concertDateId));

        // when & then
        mockMvc.perform(get("/api/v1/concerts/dates/{concertDateId}/seats", concertDateId))
                .andExpect(status().isNotFound());
    }
}
