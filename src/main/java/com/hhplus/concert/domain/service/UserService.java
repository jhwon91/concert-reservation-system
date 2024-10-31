package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.UserRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));
    }

    @Transactional
    public User chargePoint(long userId, long amount) {
        User user = findUserById(userId);
        user.chargePoint(amount);
        return user;
    }

    public void checkComparePoint(User user, ConcertDetails concertDetails) {
        if(user.getPoint() < concertDetails.getPrice()) {
            throw new CoreException(ErrorType.INSUFFICIENT_USER_BALANCE,user.getPoint());
        }
    }

    public User usePoint(User user, ConcertDetails concertDetails) {
        if(concertDetails.getPrice() <= 0){
            throw new CoreException(ErrorType.INVALID_CONCERT_PRICE,concertDetails.getPrice());
        }
        User useUser = User.builder()
                .id(user.getId())
                .name(user.getName())
                .point(user.getPoint() - concertDetails.getPrice())
                .build();
        return useUser;
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}