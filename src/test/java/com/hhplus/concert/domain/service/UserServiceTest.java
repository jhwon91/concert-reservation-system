package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.UserRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import com.hhplus.concert.testFixture.ConcertDetailsFixture;
import com.hhplus.concert.testFixture.ConcertFixture;
import com.hhplus.concert.testFixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 사용자_ID로_사용자_정보_조회(){
        User user = UserFixture.createUser(1L, "test", 1000L);
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test", result.getName());
        assertEquals(1000L, result.getPoint());
    }

    @Test
    void 사용자_ID로_사용자_정보_조회_예외발생(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CoreException exception = assertThrows(CoreException.class, ()-> userService.findUserById(1L));

        assertEquals(ErrorType.USER_NOT_FOUND, exception.getErrorType());
        assertEquals(1L, exception.getPayload());
    }

    @Test
    void 충전_성공(){
        long point = 1000L;
        long charge = 2000;
        User user = UserFixture.createUser(1L, "test",point);
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        User result = userService.chargePoint(1L, charge);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(point + charge, result.getPoint());
    }

    @Test
    void 음수충전_예외발생(){
        long charge = -2000;
        User user = UserFixture.createUser(1L, "test",1000L);
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        CoreException exception = assertThrows(CoreException.class,()-> userService.chargePoint(1L, charge));

        assertEquals(ErrorType.INVALID_CHARGE_AMOUNT, exception.getErrorType());
        assertEquals(charge, exception.getPayload());
    }

    @Test
    void 잔액충분_정상() {
        User user = UserFixture.createUser(1L, "test", 10000L);
        Concert concert = ConcertFixture.createConcert(1L, "testConcert");
        ConcertDetails concertDetails =  ConcertDetailsFixture.createConcertDetail(concert, LocalDate.of(2024,10,01), 5000L);

        assertDoesNotThrow(()-> userService.checkComparePoint(user, concertDetails));
    }

    @Test
    void 잔액부족_예외발생() {
        User user = UserFixture.createUser(1L, "test", 1000L);
        Concert concert = ConcertFixture.createConcert(1L, "testConcert");
        ConcertDetails concertDetails =  ConcertDetailsFixture.createConcertDetail(concert, LocalDate.of(2024,10,01), 5000L);

        CoreException exception = assertThrows(CoreException.class, ()->userService.checkComparePoint(user, concertDetails));

        assertEquals(ErrorType.INSUFFICIENT_USER_BALANCE, exception.getErrorType());
        assertEquals(1000L, exception.getPayload());
    }

    @Test
    void 포인트차감_정상구매() {
        long userPoint = 1000L;
        long concertPrice = 500L;

        User user = UserFixture.createUser(1L, "test", userPoint);
        Concert concert = ConcertFixture.createConcert(1L, "testConcert");
        ConcertDetails concertDetails =  ConcertDetailsFixture.createConcertDetail(concert, LocalDate.of(2024,10,01), concertPrice);

        User result = userService.usePoint(user, concertDetails);

        assertEquals(userPoint - concertPrice,result.getPoint());
    }

    @Test
    void 잘못된콘서트가격_예외발생() {
        long userPoint = 1000L;
        long concertPrice = -500L;

        User user = UserFixture.createUser(1L, "test", userPoint);
        Concert concert = ConcertFixture.createConcert(1L, "testConcert");
        ConcertDetails concertDetails =  ConcertDetailsFixture.createConcertDetail(concert, LocalDate.of(2024,10,01), concertPrice);

        CoreException exception = assertThrows(CoreException.class, ()->userService.usePoint(user, concertDetails));

        assertEquals(ErrorType.INVALID_CONCERT_PRICE, exception.getErrorType());
        assertEquals(concertPrice, exception.getPayload());
    }




}