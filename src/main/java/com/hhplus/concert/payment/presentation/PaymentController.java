package com.hhplus.concert.payment.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.payment.application.dto.PaymentResponse;
import com.hhplus.concert.payment.application.dto.ProcessPaymentCommand;
import com.hhplus.concert.payment.application.service.ProcessPaymentService;
import com.hhplus.concert.payment.presentation.dto.ProcessPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hhplus.concert.common.config.SwaggerConfig.QUEUE_TOKEN_SCHEME;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentService processPaymentService;

    @Operation(summary = "결제", description = "포인트를 차감하고 예약을 CONFIRMED로 전환합니다. 토큰은 EXPIRED 처리됩니다.")
    @SecurityRequirement(name = QUEUE_TOKEN_SCHEME)
    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> processPayment(
            @RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = processPaymentService.process(
                new ProcessPaymentCommand(request.userId(), request.reservationId(), request.paymentMethod()));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
