package com.hhplus.concert.user.domain.repository;

import com.hhplus.concert.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
}
