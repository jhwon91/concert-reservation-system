package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Concert;
import com.hhplus.concert.domain.repository.ConcertRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.EntityNotFoundException;
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
            throw new CoreException(ErrorType.CONCERT_NOT_FOUND, id);
        }
    }

    public Concert getConcert(long id){
        return concertRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.CONCERT_NOT_FOUND, id));
    }

    public Concert save(Concert concert) {
        return concertRepository.save(concert);
    }
}
