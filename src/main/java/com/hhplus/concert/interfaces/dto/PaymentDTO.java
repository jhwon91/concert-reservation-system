package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.application.dto.PaymentCommand;
import com.hhplus.concert.application.dto.PaymentResult;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public class PaymentDTO {

    @Builder
    public record PaymentResponseDTO(
            Long userId,
            String userName,
            Long point,
            SeatStatus status
    ) {
        public static PaymentResponseDTO from(PaymentResult.PaymentConcert result) {
            return PaymentResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .status(result.status())
                    .build();
        }
    }

    public record PaymentRequestDTO(
            @RequestHeader("Authorization") UUID token,
            @RequestBody PaymentRequestBody paymentRequestBody
    ) {
        public PaymentCommand.PaymentConcert toCommand() {
            return PaymentCommand.PaymentConcert.builder()
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
