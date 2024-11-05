package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.application.dto.PaymentCommand;
import com.hhplus.concert.application.dto.PaymentResult;
import com.hhplus.concert.application.dto.UserCommand;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public class PaymentDTO {

    @Builder
    public record paymentResponseDTO(
            Long userId,
            String userName,
            Long point,
            SeatStatus status
    ) {
        public static paymentResponseDTO from(PaymentResult.paymentConcert result) {
            return paymentResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .status(result.status())
                    .build();
        }
    }

    @Builder
    public record paymentRequestDTO(
            @RequestHeader("Authorization") UUID token,
            @RequestBody PaymentRequestBody paymentRequestBody
    ) {
        public PaymentCommand.payment toCommand() {
            return PaymentCommand.payment.builder()
                    .token(token)
                    .userId(paymentRequestBody.userId())
                    .reservationId(paymentRequestBody().reservationId())
                    .build();
        }
    }

    public record PaymentRequestBody(
            Long userId,
            Long reservationId
    ) {}


}
