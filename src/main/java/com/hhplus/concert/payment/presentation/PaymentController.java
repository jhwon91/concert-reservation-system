package com.hhplus.concert.payment.presentation;

import com.hhplus.concert.common.response.CommonResponse;
import com.hhplus.concert.payment.application.dto.PaymentResponse;
import com.hhplus.concert.payment.application.dto.ProcessPaymentCommand;
import com.hhplus.concert.payment.application.service.ProcessPaymentService;
import com.hhplus.concert.payment.presentation.dto.ProcessPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentService processPaymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> processPayment(
            @RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = processPaymentService.process(
                new ProcessPaymentCommand(request.userId(), request.reservationId(), request.paymentMethod()));
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
