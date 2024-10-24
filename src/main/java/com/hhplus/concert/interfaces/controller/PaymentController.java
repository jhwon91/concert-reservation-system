package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.interfaces.dto.PaymentRequestDTO;
import com.hhplus.concert.interfaces.dto.PaymentResponseDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    /**
     * 8. 결제 진행
     */
    @PostMapping("/payments")
    public PaymentResponseDTO paymentConcert(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequestDTO request
    ) {

        return new PaymentResponseDTO(
                request.user_id(),
                "유저1",
                2000L,
                "COMPLETED"
        );
    }
}
