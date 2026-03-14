package com.hhplus.concert.concert.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.application.dto.ConcertDateResponse;
import com.hhplus.concert.concert.application.dto.GetAvailableDatesQuery;
import com.hhplus.concert.concert.domain.repository.ConcertDateRepository;
import com.hhplus.concert.concert.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailableDatesService {

    private final ConcertRepository concertRepository;
    private final ConcertDateRepository concertDateRepository;

    @Transactional(readOnly = true)
    public List<ConcertDateResponse> getDates(GetAvailableDatesQuery query) {
        concertRepository.findById(query.concertId())
                .orElseThrow(() -> new CoreException(ErrorType.CONCERT_NOT_FOUND, query.concertId()));

        return concertDateRepository.findAvailableDates(query.concertId(), LocalDate.now())
                .stream()
                .map(ConcertDateResponse::from)
                .toList();
    }
}
