package com.hhplus.concert.user.infrastructure;

import com.hhplus.concert.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {
}
