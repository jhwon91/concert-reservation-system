package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConcertService {
    private final ConcertRepository concertRepository;

    @Autowired
    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public void existConcert(long id){
        if (!concertRepository.exists(id)){
            throw new IllegalArgumentException("콘서트를 찾을수 없습니다.");
        }
    }

    public Concert getConcert(long id){
        return concertRepository.findById(id);
    }
}
