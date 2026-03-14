package com.hhplus.concert.concert.infrastructure;

import com.hhplus.concert.concert.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaConcertRepository extends JpaRepository<Concert, UUID> {
}
