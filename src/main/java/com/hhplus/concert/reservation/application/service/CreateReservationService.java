package com.hhplus.concert.reservation.application.service;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.concert.domain.service.ConcertDomainService;
import com.hhplus.concert.queue.domain.QueueToken;
import com.hhplus.concert.queue.domain.repository.QueueTokenRepository;
import com.hhplus.concert.reservation.application.dto.CreateReservationCommand;
import com.hhplus.concert.reservation.application.dto.ReservationResponse;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateReservationService {

    private final QueueTokenRepository queueTokenRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ConcertDomainService concertDomainService;

    @Transactional
    public ReservationResponse create(CreateReservationCommand command) {
        QueueToken token = queueTokenRepository.findByTokenValue(command.tokenValue())
                .orElseThrow(() -> new CoreException(ErrorType.TOKEN_NOT_FOUND, command.tokenValue()));

        if (!token.isActive()) {
            throw new CoreException(ErrorType.QUEUE_TOKEN_INVALID_STATUS, token.getStatus());
        }

        Seat seat = seatRepository.findByIdWithLock(command.seatId())
                .orElseThrow(() -> new CoreException(ErrorType.SEAT_NOT_FOUND, command.seatId()));

        concertDomainService.validateSeatAvailable(seat);
        seat.reserve();

        Reservation reservation = new Reservation(
                command.userId(),
                seat.getSeatId(),
                seat.getConcertDateId(),
                command.tokenValue()
        );

        seatRepository.save(seat);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }
}
