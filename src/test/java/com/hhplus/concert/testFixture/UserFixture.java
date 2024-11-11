package com.hhplus.concert.testFixture;

import com.hhplus.concert.domain.entity.Queue;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFixture {
    public static User createUser(long userId, String userName, long point) {
        return User.builder()
                .id(userId)
                .name(userName)
                .point(point)
                .build();
    }

    public static List<User> createdUserList (long userNum){
        List<User> userList = new ArrayList<>();

        for(long i = 1; i <= userNum; i++){
            User user =  User.builder()
                    .name("Test User " + i)
                    .point(i*100)
                    .build();

            userList.add(user);
        }

        return userList;
    }
}
