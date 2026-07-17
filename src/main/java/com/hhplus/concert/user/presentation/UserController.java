package com.hhplus.concert.user.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.user.application.dto.ChargePointCommand;
import com.hhplus.concert.user.application.dto.GetUserPointQuery;
import com.hhplus.concert.user.application.dto.UserPointResult;
import com.hhplus.concert.user.application.service.ChargePointService;
import com.hhplus.concert.user.application.service.GetUserPointService;
import com.hhplus.concert.user.presentation.dto.ChargePointRequest;
import com.hhplus.concert.user.presentation.dto.UserPointResponse;
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
    public ResponseEntity<CommonResponse<UserPointResponse>> getPoint(@PathVariable UUID userId) {
        UserPointResult result = getUserPointService.getPoint(new GetUserPointQuery(userId));
        return ResponseEntity.ok(CommonResponse.success(UserPointResponse.from(result)));
    }

    @Operation(summary = "잔액 충전")
    @PatchMapping("/{userId}/point")
    public ResponseEntity<CommonResponse<UserPointResponse>> chargePoint(@PathVariable UUID userId, @RequestBody ChargePointRequest request) {
        UserPointResult result = chargePointService.charge(
                new ChargePointCommand(userId, request.amount()));
        return ResponseEntity.ok(CommonResponse.success(UserPointResponse.from(result)));
    }
}
