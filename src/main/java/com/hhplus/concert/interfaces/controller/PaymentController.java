package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.PaymentFacade;
import com.hhplus.concert.interfaces.dto.PaymentDTO;
import com.hhplus.concert.interfaces.dto.PaymentRequestDTO;
import com.hhplus.concert.interfaces.dto.PaymentResponseDTO;
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
    public PaymentDTO.paymentResponseDTO paymentConcert(PaymentDTO.paymentRequestDTO request) {
        return PaymentDTO.paymentResponseDTO.from(paymentFacade.paymentConcert(request.toCommand()));
    }
}
