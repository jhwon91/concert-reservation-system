package com.hhplus.concert.interfaces.dto;

public record ReservationRequestDTO(
        Long user_id,
        Long concert_detail_id,
        Long seat_id,
        String concert_date
) {}