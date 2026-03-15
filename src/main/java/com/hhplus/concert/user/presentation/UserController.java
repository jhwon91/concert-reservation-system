package com.hhplus.concert.user.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.user.application.dto.ChargePointCommand;
import com.hhplus.concert.user.application.dto.GetUserPointQuery;
import com.hhplus.concert.user.application.dto.UserPointResponse;
import com.hhplus.concert.user.application.service.ChargePointService;
import com.hhplus.concert.user.application.service.GetUserPointService;
import com.hhplus.concert.user.presentation.dto.ChargePointRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User", description = "사용자 포인트 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserPointService getUserPointService;
    private final ChargePointService chargePointService;

    @Operation(summary = "잔액 조회")
    @GetMapping("/{userId}/point")
    public ResponseEntity<CommonResponse<UserPointResponse>> getPoint(
            @PathVariable UUID userId) {
        UserPointResponse response = getUserPointService.getPoint(new GetUserPointQuery(userId));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "잔액 충전")
    @PatchMapping("/{userId}/point")
    public ResponseEntity<CommonResponse<UserPointResponse>> chargePoint(
            @PathVariable UUID userId,
            @RequestBody ChargePointRequest request) {
        UserPointResponse response = chargePointService.charge(
                new ChargePointCommand(userId, request.amount()));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
