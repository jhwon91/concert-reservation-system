package com.hhplus.concert.application;

import com.hhplus.concert.application.dto.ConcertResult;
import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.service.ConcertDetailService;
import com.hhplus.concert.domain.service.ConcertService;
import com.hhplus.concert.domain.service.QueueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertFacade {
    private final ConcertDetailService concertDetailService;
    private final ConcertService concertService;
    private final QueueService queueService;

    public ConcertFacade(ConcertDetailService concertDetailService, ConcertService concertService, QueueService queueService) {
        this.concertDetailService = concertDetailService;
        this.concertService = concertService;
        this.queueService = queueService;
    }

    /**
     *  예약 가능한 날짜 조회
     */
    public ConcertResult.ConcertAvailableDates getAvailableConcertDates(Long concertId, String token) {
        queueService.validationToken(token);
        Concert concert = concertService.getConcert(concertId);

        List<ConcertResult.ConcertDetails> concertDetailsList = concertDetailService.getAvailableConcertDates(concert.getId()).stream()
                .map(entity -> ConcertResult.ConcertDetails.builder()
                        .concert_detail_id(entity.getId())
                        .concert_id(entity.getConcert_id())
                        .max_seat(entity.getMax_seat())
                        .price(entity.getPrice())
                        .concert_date(entity.getConcert_date())
                        .build())
                .collect(Collectors.toList());

        return ConcertResult.ConcertAvailableDates.builder()
                .concert_id(concert.getId())
                .concert_title(concert.getTitle())
                .concert_detail(concertDetailsList)
                .build();
    }

}
