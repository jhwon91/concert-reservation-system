package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.Seat;
import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.repository.SeatRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Seat getSeat(Long seatId) {
        return seatRepository.findByIdWithLock(seatId);
    }

    public void checkAvailableStatus(Seat seat) {
        if(seat.getStatus() != SeatStatus.AVAILABLE){
            throw new CoreException(ErrorType.SEAT_NOT_RESERVABLE,seat.getStatus());
        }
    }

    public void checkTemporaryStatus(Seat seat) {
        if(seat.getStatus() != SeatStatus.TEMPORARY_ALLOCATED){
            throw new CoreException(ErrorType.SEAT_NOT_PAYABLE,seat.getStatus());
        }
    }

    public Seat changeSeatStatus(Seat seat, SeatStatus status) {
        return Seat.builder()
                .id(seat.getId())
                .concertDetailId(seat.getConcertDetailId())
                .seat_number(seat.getSeat_number())
                .status(status)
                .build();
    }


    public Seat save(Seat seat){
        return seatRepository.save(seat);
    }
}
