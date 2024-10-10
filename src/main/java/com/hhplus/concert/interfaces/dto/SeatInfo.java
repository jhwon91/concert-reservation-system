package com.hhplus.concert.interfaces.dto;

public record SeatInfo(
        Long seat_id,
        Integer seat_number,
        String status
) {}