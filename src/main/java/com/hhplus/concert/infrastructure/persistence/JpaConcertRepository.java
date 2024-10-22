package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConcertRepository extends JpaRepository<Concert, Long> {
}
