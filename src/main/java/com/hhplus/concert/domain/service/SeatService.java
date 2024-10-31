package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.SeatRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAvailableSeat(Long concetDetailId, SeatStatus status) {
        return seatRepository.findByConcertDetailIdAndStatus(concetDetailId,status);
    }

    @Transactional
    public Seat findAvailableSeat(Long seatId) {
        Seat seat = getSeat(seatId);
        checkAvailableStatus(seat);

        return seat;
    }

    public Seat getSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(()-> new CoreException(ErrorType.SEAT_NOT_FOUND, seatId));
    }

    public void checkAvailableStatus(Seat seat) {
        seat.checkAvailableStatus();
    }

    public void checkTemporaryStatus(Seat seat) {
        seat.checkTemporaryStatus();
    }

    @Transactional
    public void changeSeatStatus(Seat seat, SeatStatus status) {
        seat.changeStatus(status);
    }

    public Seat save(Seat seat){
        return seatRepository.save(seat);
    }
}
