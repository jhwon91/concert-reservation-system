package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.Reservation;
import com.hhplus.concert.domain.repository.ReservationRepository;
import com.hhplus.concert.infrastructure.persistence.JpaReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final JpaReservationRepository jpaReservationRepository;

    public ReservationRepositoryImpl(JpaReservationRepository jpaReservationRepository) {
        this.jpaReservationRepository = jpaReservationRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpaReservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return jpaReservationRepository.findById(id);
    }
}
