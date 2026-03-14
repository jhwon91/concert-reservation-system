package com.hhplus.concert.concert.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.application.dto.GetAvailableSeatsQuery;
import com.hhplus.concert.concert.application.dto.SeatResponse;
import com.hhplus.concert.concert.domain.SeatStatus;
import com.hhplus.concert.concert.domain.repository.ConcertDateRepository;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAvailableSeatsService {

    private final ConcertDateRepository concertDateRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(GetAvailableSeatsQuery query) {
        concertDateRepository.findById(query.concertDateId())
                .orElseThrow(() -> new CoreException(ErrorType.CONCERT_DATE_NOT_FOUND, query.concertDateId()));

        return seatRepository.findByConcertDateIdAndStatus(query.concertDateId(), SeatStatus.AVAILABLE)
                .stream()
                .map(SeatResponse::from)
                .toList();
    }
}
