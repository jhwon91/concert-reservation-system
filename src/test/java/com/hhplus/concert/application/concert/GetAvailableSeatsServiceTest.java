package com.hhplus.concert.application.concert;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.application.dto.GetAvailableSeatsQuery;
import com.hhplus.concert.concert.application.dto.SeatResponse;
import com.hhplus.concert.concert.application.service.GetAvailableSeatsService;
import com.hhplus.concert.concert.domain.ConcertDate;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.SeatStatus;
import com.hhplus.concert.concert.domain.repository.ConcertDateRepository;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetAvailableSeatsServiceTest {

    @InjectMocks
    private GetAvailableSeatsService getAvailableSeatsService;

    @Mock
    private ConcertDateRepository concertDateRepository;

    @Mock
    private SeatRepository seatRepository;

    @Test
    @DisplayName("예약 가능한 좌석 목록을 반환한다")
    void getSeats_success() {
        // given
        UUID concertDateId = UUID.randomUUID();
        ConcertDate concertDate = new ConcertDate(UUID.randomUUID(), LocalDate.now().plusDays(1), 50);
        Seat seat1 = new Seat(concertDateId, SeatNumber.of(1), BigDecimal.valueOf(150_000));
        Seat seat2 = new Seat(concertDateId, SeatNumber.of(2), BigDecimal.valueOf(150_000));

        given(concertDateRepository.findById(concertDateId)).willReturn(Optional.of(concertDate));
        given(seatRepository.findByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE))
                .willReturn(List.of(seat1, seat2));

        // when
        List<SeatResponse> result = getAvailableSeatsService.getSeats(new GetAvailableSeatsQuery(concertDateId));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 날짜 조회 시 예외가 발생한다")
    void getSeats_concert_date_not_found_throws() {
        // given
        UUID concertDateId = UUID.randomUUID();
        given(concertDateRepository.findById(concertDateId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getAvailableSeatsService.getSeats(new GetAvailableSeatsQuery(concertDateId)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.CONCERT_DATE_NOT_FOUND);
    }
}
