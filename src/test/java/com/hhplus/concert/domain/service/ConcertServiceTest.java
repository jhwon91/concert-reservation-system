package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.repository.ConcertRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import com.hhplus.concert.testFixture.ConcertFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class  ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 콘서트ID로_존재하는_콘서트_정보_조회_성공(){
        Concert concert = ConcertFixture.createConcert(1L, "test");
        when(concertRepository.findById(eq(1L))).thenReturn(Optional.of(concert));

        Concert result = concertService.getConcert(1L);

        assertNotNull(result);
        assertEquals(concert.getId(), result.getId());
    }

    @Test
    void 콘서트ID로_존재하지_않는_콘서트_정보_조회_예외발생(){
        when(concertRepository.findById(anyLong())).thenReturn(Optional.empty());

        CoreException exception = assertThrows(CoreException.class, () -> concertService.getConcert(1L));

        assertEquals(ErrorType.CONCERT_NOT_FOUND, exception.getErrorType());
        assertEquals(1L, exception.getPayload());
    }

    @Test
    void 존재하는_콘서트_정보_조회_성공(){
        when(concertRepository.exists(anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> concertService.existConcert(1L));
    }

    @Test
    void 존재하지_않는_콘서트_정보_조회_예외발생(){
        when(concertRepository.exists(anyLong())).thenReturn(false);
        CoreException exception = assertThrows(CoreException.class, () -> concertService.getConcert(1L));

        assertEquals(ErrorType.CONCERT_NOT_FOUND, exception.getErrorType());
        assertEquals(1L, exception.getPayload());
    }

}