package com.hhplus.concert.reservation.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.reservation.application.dto.CancelReservationCommand;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void cancel(CancelReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new CoreException(ErrorType.RESERVATION_NOT_FOUND, command.reservationId()));

        if (!reservation.getUserId().equals(command.userId())) {
            throw new CoreException(ErrorType.RESERVATION_ACCESS_DENIED, command.userId());
        }

        Seat seat = seatRepository.findById(reservation.getSeatId())
                .orElseThrow(() -> new CoreException(ErrorType.SEAT_NOT_FOUND, reservation.getSeatId()));

        reservation.cancel();
        seat.release();

        reservationRepository.save(reservation);
        seatRepository.save(seat);
    }
}
