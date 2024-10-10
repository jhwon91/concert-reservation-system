package com.hhplus.concert.interfaces.dto;

import java.util.List;

public record SeatsResponseDTO (
        Long concert_id,
        String concert_title,
        Long concert_detail_id,
        String concert_date,
        Integer max_seat,
        List<SeatInfo> seats
) {}
