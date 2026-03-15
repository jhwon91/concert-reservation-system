package com.hhplus.concert.reservation.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.reservation.application.dto.CreateReservationCommand;
import com.hhplus.concert.reservation.application.dto.ReservationResponse;
import com.hhplus.concert.reservation.application.service.CreateReservationService;
import com.hhplus.concert.reservation.presentation.dto.CreateReservationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hhplus.concert.common.config.SwaggerConfig.QUEUE_TOKEN_SCHEME;

@Tag(name = "Reservation", description = "좌석 예약 API")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final String TOKEN_HEADER = "X-Queue-Token";

    private final CreateReservationService createReservationService;

    @Operation(summary = "좌석 예약", description = "좌석을 임시 점유(5분)하고 PENDING 예약을 생성합니다.")
    @SecurityRequirement(name = QUEUE_TOKEN_SCHEME)
    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponse>> createReservation(
            @RequestHeader(TOKEN_HEADER) String tokenValue,
            @RequestBody CreateReservationRequest request) {
        ReservationResponse response = createReservationService.create(
                new CreateReservationCommand(request.userId(), request.seatId(), tokenValue));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
