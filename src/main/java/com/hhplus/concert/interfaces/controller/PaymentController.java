package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.PaymentFacade;
import com.hhplus.concert.interfaces.dto.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @Autowired
    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    /**
     * 8. 결제 진행
     */
    @PostMapping("/payments")
    public PaymentDTO.PaymentResponseDTO paymentConcert(PaymentDTO.PaymentRequestDTO request) {
        return PaymentDTO.PaymentResponseDTO.from(paymentFacade.paymentConcert(request.toCommand()));
    }
}
