package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.Reservation;

import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById (long id);
}
