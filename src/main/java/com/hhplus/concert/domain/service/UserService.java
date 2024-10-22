package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
    }

    public User chargePoint(User user, long amount) {
       if(amount <= 0){
           throw new IllegalArgumentException("충전액이 0보다 작습니다.");
       }
       User chargeUser = User.builder()
               .id(user.getId())
               .name(user.getName())
               .point(user.getPoint() + amount)
               .build();
       return chargeUser;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}