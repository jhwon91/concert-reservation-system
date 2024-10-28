package com.hhplus.concert.infrastructure.persistence.impl;

import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.UserRepository;
import com.hhplus.concert.infrastructure.persistence.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Autowired
    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(long id) {
        return jpaUserRepository.findById(id);
    }
}
