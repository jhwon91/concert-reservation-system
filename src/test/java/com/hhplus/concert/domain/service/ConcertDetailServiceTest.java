package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.ConcertDetailRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import com.hhplus.concert.testFixture.ConcertDetailsFixture;
import com.hhplus.concert.testFixture.ConcertFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ConcertDetailServiceTest {

    @Mock
    private ConcertDetailRepository concertDetailRepository;

    @InjectMocks
    private ConcertDetailService concertDetailService;

    private Concert concert;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        concert = ConcertFixture.createConcert(1L, "testConcert");
    }

    @Test
    void 예약_가능한_콘서트_일정_조회() {

        List<ConcertDetails> ConcertDetailsList = ConcertDetailsFixture.createConcertDetailList(concert,LocalDate.of(2024, 10, 30));

        when(concertDetailRepository.findAvailableConcertDatesByConcertId(eq(concert.getId()), eq(SeatStatus.AVAILABLE)))
                .thenReturn(ConcertDetailsList);

        List<ConcertDetails> result = concertDetailService.getAvailableConcertDates(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 10, 30), result.get(0).getConcertDate());
        assertEquals(LocalDate.of(2024, 10, 31), result.get(1).getConcertDate());
    }

    @Test
    void 존재하는_콘서트_상세정보_조회_성공() {
        when(concertDetailRepository.exists(anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> concertDetailService.existConcertDetail(1L));
    }

    @Test
    void 존재하지_않는_콘서트_상세정보_조회_예외발생() {
        when(concertDetailRepository.exists(anyLong())).thenReturn(false);

        CoreException exception = assertThrows(CoreException.class, () -> concertDetailService.existConcertDetail(1L));

        assertEquals(ErrorType.CONCERT_DETAIL_NOT_FOUND, exception.getErrorType());
        assertEquals(1L, exception.getPayload());
    }

    @Test
    void 콘서트_상세정보_ID로_존재하는_콘서트_상세정보_조회_성공() {
        ConcertDetails detail1 =  ConcertDetailsFixture.createConcertDetail(concert,LocalDate.of(2024,10,30));

        when(concertDetailRepository.findById(1L)).thenReturn(Optional.of(detail1));

        ConcertDetails result = concertDetailService.getConcertDetail(1L);

        assertNotNull(result);
        assertEquals(detail1, result);
        assertEquals(concert.getId(), result.getId());
        assertEquals(10L, result.getMaxSeat());
        assertEquals(100L, result.getPrice());
        assertEquals(LocalDate.of(2024, 10, 30), result.getConcertDate());
    }

    @Test
    void 콘서트_상세정보_ID로_존재하지_않는_콘서트_상세정보_조회_예외발생() {

        when(concertDetailRepository.findById(anyLong())).thenReturn(Optional.empty());

        CoreException exception = assertThrows(CoreException.class, () -> concertDetailService.getConcertDetail(1L));

        assertEquals(ErrorType.CONCERT_DETAIL_NOT_FOUND, exception.getErrorType());
        assertEquals(1L, exception.getPayload());
    }
}