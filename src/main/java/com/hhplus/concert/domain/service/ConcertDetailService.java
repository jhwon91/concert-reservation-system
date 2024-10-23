package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.ConcertDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcertDetailService {
    private final ConcertDetailRepository concertDetailRepository;

    @Autowired
    public ConcertDetailService(ConcertDetailRepository concertDetailRepository) {
        this.concertDetailRepository = concertDetailRepository;
    }

    public List<ConcertDetails> getAvailableConcertDates(long concertId) {
        return concertDetailRepository.findAvailableConcertDatesByConcertId(concertId, SeatStatus.AVAILABLE);
    }

    public void existConcertDetail(long id){
        if (!concertDetailRepository.exists(id)){
            throw new IllegalArgumentException("콘서트를 찾을수 없습니다.");
        }
    }

    public ConcertDetails getConcertDetail(long id){
        return concertDetailRepository.findById(id);
    }

}
