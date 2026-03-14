package com.hhplus.concert.common.scheduler;

import com.hhplus.concert.concert.domain.Seat;
import com.hhplus.concert.concert.domain.repository.SeatRepository;
import com.hhplus.concert.reservation.domain.Reservation;
import com.hhplus.concert.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRateString = "${queue.scheduler.reservation-expiry-interval}")
    @Transactional
    public void cancelExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredPendingReservations(LocalDateTime.now());

        expiredReservations.forEach(reservation -> {
            reservation.cancel();
            reservationRepository.save(reservation);

            seatRepository.findById(reservation.getSeatId()).ifPresent(seat -> {
                seat.release();
                seatRepository.save(seat);
            });
        });

        if (!expiredReservations.isEmpty()) {
            log.debug("예약 만료 처리: {}개 예약 취소", expiredReservations.size());
        }
    }
}
