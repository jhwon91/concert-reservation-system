package com.hhplus.concert.testFixture;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.entity.ConcertDetails;

import java.time.LocalDate;
import java.util.List;

public class ConcertDetailsFixture {
    public static List<ConcertDetails> createConcertDetailList (Concert concert, LocalDate date){
        ConcertDetails detail1 =  ConcertDetails.builder()
                .id(1L)
                .concertId(concert.getId())
                .maxSeat(10L)
                .price(100L)
                .concertDate(date)
                .build();
        ConcertDetails detail2 =  ConcertDetails.builder()
                .id(2L)
                .concertId(concert.getId())
                .maxSeat(10L)
                .price(100L)
                .concertDate(date.plusDays(1))
                .build();

        return List.of(detail1, detail2);
    }

    public static ConcertDetails createConcertDetail (Concert concert, LocalDate date){
        return ConcertDetails.builder()
                .id(1L)
                .concertId(concert.getId())
                .maxSeat(10L)
                .price(100L)
                .concertDate(date)
                .build();
    }

    public static ConcertDetails createConcertDetail (Concert concert, LocalDate date, long point){
        return ConcertDetails.builder()
                .id(1L)
                .concertId(concert.getId())
                .maxSeat(10L)
                .price(point)
                .concertDate(date)
                .build();
    }
}
