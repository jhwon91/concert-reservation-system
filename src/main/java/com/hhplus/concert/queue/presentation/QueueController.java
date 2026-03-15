package com.hhplus.concert.queue.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.queue.application.dto.GetQueueStatusQuery;
import com.hhplus.concert.queue.application.dto.IssueQueueTokenCommand;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.application.service.GetQueueStatusService;
import com.hhplus.concert.queue.application.service.IssueQueueTokenService;
import com.hhplus.concert.queue.presentation.dto.IssueTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Queue", description = "대기열 토큰 API")
@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final IssueQueueTokenService issueQueueTokenService;
    private final GetQueueStatusService getQueueStatusService;

    @Operation(summary = "토큰 발급", description = "대기열에 등록하고 WAITING 토큰을 반환합니다.")
    @PostMapping("/tokens")
    public ResponseEntity<CommonResponse<QueueTokenResponse>> issueToken(
            @RequestBody IssueTokenRequest request) {
        QueueTokenResponse response = issueQueueTokenService.issue(
                new IssueQueueTokenCommand(request.userId()));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "대기 순번 조회", description = "토큰의 현재 상태 및 대기 순번을 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<CommonResponse<QueueTokenResponse>> getStatus(
            @RequestParam String token) {
        QueueTokenResponse response = getQueueStatusService.getStatus(
                new GetQueueStatusQuery(token));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
