package com.hhplus.concert.domain.repository;

import com.hhplus.concert.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(long id);
    User deleteUserById(long userId);
}

