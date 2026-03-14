package com.hhplus.concert.concert.application;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.application.dto.ConcertDateResponse;
import com.hhplus.concert.concert.application.dto.GetAvailableDatesQuery;
import com.hhplus.concert.concert.application.service.GetAvailableDatesService;
import com.hhplus.concert.concert.domain.Concert;
import com.hhplus.concert.concert.domain.ConcertDate;
import com.hhplus.concert.concert.domain.repository.ConcertDateRepository;
import com.hhplus.concert.concert.domain.repository.ConcertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetAvailableDatesServiceTest {

    @InjectMocks
    private GetAvailableDatesService getAvailableDatesService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertDateRepository concertDateRepository;

    @Test
    @DisplayName("예약 가능한 날짜 목록을 반환한다")
    void getDates_success() {
        // given
        Concert concert = new Concert("테스트 콘서트", "올림픽공원", "설명");
        UUID concertId = concert.getConcertId();
        ConcertDate date1 = new ConcertDate(concertId, LocalDate.now().plusDays(1), 50);
        ConcertDate date2 = new ConcertDate(concertId, LocalDate.now().plusDays(2), 50);

        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
        given(concertDateRepository.findAvailableDates(eq(concertId), any())).willReturn(List.of(date1, date2));

        // when
        List<ConcertDateResponse> result = getAvailableDatesService.getDates(new GetAvailableDatesQuery(concertId));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).availableSeats()).isEqualTo(50);
    }

    @Test
    @DisplayName("예약 가능한 날짜가 없으면 빈 목록을 반환한다")
    void getDates_empty() {
        // given
        Concert concert = new Concert("테스트 콘서트", "올림픽공원", "설명");
        UUID concertId = concert.getConcertId();

        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
        given(concertDateRepository.findAvailableDates(eq(concertId), any())).willReturn(List.of());

        // when
        List<ConcertDateResponse> result = getAvailableDatesService.getDates(new GetAvailableDatesQuery(concertId));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 조회 시 예외가 발생한다")
    void getDates_concert_not_found_throws() {
        // given
        UUID concertId = UUID.randomUUID();
        given(concertRepository.findById(concertId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getAvailableDatesService.getDates(new GetAvailableDatesQuery(concertId)))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.CONCERT_NOT_FOUND);
    }
}
