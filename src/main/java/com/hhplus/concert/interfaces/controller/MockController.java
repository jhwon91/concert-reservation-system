package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.interfaces.dto.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mock/api")
public class MockController {
    /**
     * 1. 토큰 발급
     */
    @PostMapping("/tokens")
    public TokenResponseDTO issueToken(@RequestBody TokenRequestDTO request) {
        return new TokenResponseDTO(UUID.randomUUID().toString(), "WAIT", 5);
    }

    /**
     * 2. 대기열 상태 확인
     */
    @GetMapping("/tokens/status")
    public TokenStatusResponseDTO getTokenStatus(@RequestHeader("Authorization") String waitingId) {
        // 간단한 Mock 데이터 반환
        return new TokenStatusResponseDTO("WAIT", 5);
    }

    /**
     * 3. 예약 가능한 날짜 조회
     */
    @GetMapping("/reservations/dates/{concert_id}")
    public ConcertDatesResponseDTO getConcertDates(
            @PathVariable("concert_id") Long concertId,
            @RequestHeader("Authorization") String waitingId) {

        List<ConcertDetail> concertDetails = Arrays.asList(
                new ConcertDetail(1L, "2024-10-01"),
                new ConcertDetail(2L, "2024-10-02")
        );

        return new ConcertDatesResponseDTO(concertId, "콘서트1", concertDetails);
    }

    /**
     * 4. 특정 날짜의 예약 가능한 좌석 조회
     */
    @GetMapping("/reservations/seats")
    public SeatsResponseDTO getAvailableSeats(
            @RequestParam("concert_detail_id") Long concertDetailId,
            @RequestParam("concert_date") String concertDate,
            @RequestHeader("Authorization") String waitingId) {

        List<SeatInfo> seats = Arrays.asList(
                new SeatInfo(1L, 1, "AVAILABLE"),
                new SeatInfo(2L, 2, "UNAVAILABLE")
        );

        return new SeatsResponseDTO(1L, "콘서트1", concertDetailId, concertDate, 50, seats);
    }

    /**
     * 5. 좌석 예약
     */
    @PostMapping("/reservations")
    public ReservationResponseDTO reserveSeat(
            @RequestBody ReservationRequestDTO request,
            @RequestHeader("Authorization") String waitingId) {

        return new ReservationResponseDTO(
                1L,
                request.user_id(),
                "유저1",
                1L,
                "콘서트1",
                request.concert_detail_id(),
                null,
                request.seat_id(),
                1,
                "TEMPORARY_ALLOCATED",
                LocalDateTime.now()
        );
    }

    /**
     * 6. 사용자 잔액 조회
     */
    @GetMapping("/users/balance/{user_id}")
    public UserBalanceResponseDTO getUserBalance(@PathVariable("user_id") Long userId) {
        return new UserBalanceResponseDTO(userId, "유저1", 100L);
    }

    /**
     * 7. 잔액 충전
     */
    @PostMapping("/users/balance/{user_id}")
    public UserBalanceResponseDTO chargeUserBalance(
            @PathVariable("user_id") Long userId,
            @RequestBody BalanceChargeRequestDTO request) {

        return new UserBalanceResponseDTO(userId, "유저1", 2000L);
    }

    /**
     * 8. 결제 진행
     */
    @PostMapping("/payments")
    public PaymentResponseDTO processPayment(
            @RequestBody PaymentRequestDTO request,
            @RequestHeader("Authorization") String waitingId) {

        return new PaymentResponseDTO(
                request.user_id(),
                "유저1",
                2000L,
                "COMPLETED"
        );
    }
}
