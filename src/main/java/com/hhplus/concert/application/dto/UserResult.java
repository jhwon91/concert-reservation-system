package com.hhplus.concert.application.dto;

import com.hhplus.concert.domain.entity.PointHistory;
import com.hhplus.concert.domain.entity.User;
import com.hhplus.concert.domain.enums.TransactionType;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

public class UserResult {

    @Builder
    public record PointHistoryDTO (
            Long pointHistoryId,
            Long userID,
            Long amount,
            TransactionType type
    ) {
        public static PointHistoryDTO from (PointHistory pointHistory) {
            return PointHistoryDTO.builder()
                    .pointHistoryId(pointHistory.getId())
                    .userID(pointHistory.getUserId())
                    .amount(pointHistory.getAmount())
                    .type(pointHistory.getType())
                    .build();
        }
    }


    @Builder
    public record SearchPoint(
            Long userId,
            String userName,
            Long point
    ){
        public static SearchPoint from(User user){
            return SearchPoint.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .point(user.getPoint())
                    .build();
        }
    }

    @Builder
    public record ChargePoint(
            Long userId,
            String userName,
            Long point
    ){
        public static ChargePoint from(User user){
            return ChargePoint.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .point(user.getPoint())
                    .build();
        }
    }

    @Builder
    public record History(
            Long userId,
            String userName,
            Long point,
            List<PointHistoryDTO> pointHistory
    ){
        public static History from(User user, List<PointHistory> pointHistory){
            return History.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .point(user.getPoint())
                    .pointHistory(pointHistory.stream()
                            .map(PointHistoryDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }
}
