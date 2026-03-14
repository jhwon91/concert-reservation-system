package com.hhplus.concert.reservation.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.reservation.application.dto.CreateReservationCommand;
import com.hhplus.concert.reservation.application.dto.ReservationResponse;
import com.hhplus.concert.reservation.application.service.CreateReservationService;
import com.hhplus.concert.reservation.presentation.dto.CreateReservationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final String TOKEN_HEADER = "X-Queue-Token";

    private final CreateReservationService createReservationService;

    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponse>> createReservation(
            @RequestHeader(TOKEN_HEADER) String tokenValue,
            @RequestBody CreateReservationRequest request) {
        ReservationResponse response = createReservationService.create(
                new CreateReservationCommand(request.userId(), request.seatId(), tokenValue));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
