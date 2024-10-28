package com.hhplus.concert.infrastructure.persistence;

import com.hhplus.concert.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
}
