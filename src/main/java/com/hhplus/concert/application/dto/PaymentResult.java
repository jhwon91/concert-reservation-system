package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;

public class PaymentResult {

    @Builder
    public record paymentConcert(
            Long userId,
            String userName,
            Long point,
            SeatStatus status
    ) {
        public static PaymentResult.paymentConcert from(User user, Seat seat) {
            return PaymentResult.paymentConcert.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .point(user.getPoint())
                    .status(seat.getStatus())
                    .build();
        }
    }
}
