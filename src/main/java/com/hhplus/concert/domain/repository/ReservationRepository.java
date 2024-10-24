package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Reservation;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
}
