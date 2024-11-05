package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.SeatStatus;
import lombok.Builder;

public class PaymentResult {

    @Builder
    public record PaymentConcert(
            Long userId,
            String userName,
            Long point,
            SeatStatus status
    ) {
        public static PaymentConcert from(User user, Seat seat) {
            return PaymentConcert.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .point(user.getPoint())
                    .status(seat.getStatus())
                    .build();
        }
    }
}
