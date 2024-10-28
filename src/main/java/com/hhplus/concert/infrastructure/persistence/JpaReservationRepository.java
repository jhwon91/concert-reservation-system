package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
}
