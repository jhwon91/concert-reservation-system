package com.hhplus.concert.queue.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.queue.application.dto.GetQueueStatusQuery;
import com.hhplus.concert.queue.application.dto.IssueQueueTokenCommand;
import com.hhplus.concert.queue.application.dto.QueueTokenResponse;
import com.hhplus.concert.queue.application.service.GetQueueStatusService;
import com.hhplus.concert.queue.application.service.IssueQueueTokenService;
import com.hhplus.concert.queue.presentation.dto.IssueTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final IssueQueueTokenService issueQueueTokenService;
    private final GetQueueStatusService getQueueStatusService;

    @PostMapping("/tokens")
    public ResponseEntity<CommonResponse<QueueTokenResponse>> issueToken(
            @RequestBody IssueTokenRequest request) {
        QueueTokenResponse response = issueQueueTokenService.issue(
                new IssueQueueTokenCommand(request.userId()));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @GetMapping("/status")
    public ResponseEntity<CommonResponse<QueueTokenResponse>> getStatus(
            @RequestParam String token) {
        QueueTokenResponse response = getQueueStatusService.getStatus(
                new GetQueueStatusQuery(token));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
