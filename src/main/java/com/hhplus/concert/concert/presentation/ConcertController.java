package com.hhplus.concert.concert.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.concert.application.dto.*;
import com.hhplus.concert.concert.application.service.GetAvailableDatesService;
import com.hhplus.concert.concert.application.service.GetAvailableSeatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final GetAvailableDatesService getAvailableDatesService;
    private final GetAvailableSeatsService getAvailableSeatsService;

    @GetMapping("/{concertId}/dates")
    public ResponseEntity<CommonResponse<List<ConcertDateResponse>>> getAvailableDates(
            @PathVariable UUID concertId) {
        List<ConcertDateResponse> response = getAvailableDatesService.getDates(
                new GetAvailableDatesQuery(concertId));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @GetMapping("/dates/{concertDateId}/seats")
    public ResponseEntity<CommonResponse<List<SeatResponse>>> getAvailableSeats(
            @PathVariable UUID concertDateId) {
        List<SeatResponse> response = getAvailableSeatsService.getSeats(
                new GetAvailableSeatsQuery(concertDateId));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
