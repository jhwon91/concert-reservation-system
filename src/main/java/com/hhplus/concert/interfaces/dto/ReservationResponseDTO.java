package com.hhplus.concert.interfaces.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationResponseDTO(
        Long reservation_id,
        Long user_id,
        String user_name,
        Long concert_id,
        String concert_name,
        Long concert_detail_id,
        String concert_date,
        Long seat_id,
        Integer seat_number,
        String status,
        LocalDateTime reservation_at
) {}