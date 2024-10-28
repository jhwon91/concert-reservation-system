package com.hhplus.concert.domain.service;

import com.hhplus.concert.domain.entity.ConcertDetails;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.repository.UserRepository;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
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
                .orElseThrow(() -> new CoreException(ErrorType.USER_NOT_FOUND, userId));
    }

    public User chargePoint(User user, long amount) {
       if(amount <= 0){
           throw new CoreException(ErrorType.INVALID_CHARGE_AMOUNT,amount);
       }
       User chargeUser = User.builder()
               .id(user.getId())
               .name(user.getName())
               .point(user.getPoint() + amount)
               .build();
       return chargeUser;
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

    public User save(User user) {
        return userRepository.save(user);
    }
}