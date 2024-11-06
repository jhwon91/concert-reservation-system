package com.hhplus.concert.testFixture;

import com.hhplus.concert.domain.entity.User;

public class UserFixture {
    public static User createUser(long userId, String userName, long point) {
        return User.builder()
                .id(userId)
                .name(userName)
                .point(point)
                .build();
    }
}
