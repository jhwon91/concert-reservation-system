package com.hhplus.concert.testFixture;

import com.hhplus.concert.domain.entity.Concert;

public class ConcertFixture {
    public static Concert createConcert(long concertId, String title) {
        return Concert.builder()
                .id(concertId)
                .title(title)
                .build();
    }
}
