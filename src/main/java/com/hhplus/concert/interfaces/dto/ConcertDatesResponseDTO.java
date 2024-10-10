package com.hhplus.concert.interfaces.dto;

import java.util.List;

public record ConcertDatesResponseDTO(
        Long concert_id,
        String concert_title,
        List<ConcertDetail> concert_detail
) {}
