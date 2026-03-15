package com.hhplus.concert.concert.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.concert.application.dto.*;
import com.hhplus.concert.concert.application.service.GetAvailableDatesService;
import com.hhplus.concert.concert.application.service.GetAvailableSeatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Concert", description = "콘서트 조회 API")
@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final GetAvailableDatesService getAvailableDatesService;
    private final GetAvailableSeatsService getAvailableSeatsService;

    @Operation(summary = "예약 가능 날짜 조회", description = "콘서트의 예약 가능한 날짜 목록을 반환합니다.")
    @GetMapping("/{concertId}/dates")
    public ResponseEntity<CommonResponse<List<ConcertDateResponse>>> getAvailableDates(
            @PathVariable UUID concertId) {
        List<ConcertDateResponse> response = getAvailableDatesService.getDates(
                new GetAvailableDatesQuery(concertId));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "예약 가능 좌석 조회", description = "날짜별 AVAILABLE 좌석 목록(1~50번)을 반환합니다.")
    @GetMapping("/dates/{concertDateId}/seats")
    public ResponseEntity<CommonResponse<List<SeatResponse>>> getAvailableSeats(
            @PathVariable UUID concertDateId) {
        List<SeatResponse> response = getAvailableSeatsService.getSeats(
                new GetAvailableSeatsQuery(concertDateId));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
