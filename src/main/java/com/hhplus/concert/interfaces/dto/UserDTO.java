package com.hhplus.concert.interfaces.dto;

import com.hhplus.concert.application.dto.UserCommand;
import com.hhplus.concert.application.dto.UserResult;
import com.hhplus.concert.domain.entity.PointHistory;
import com.hhplus.concert.domain.enums.TransactionType;
import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {


    @Builder
    public record PointHistoryDTO (
            Long pointHistoryId,
            Long amount,
            TransactionType type
    ) {
        public static PointHistoryDTO from (UserResult.PointHistoryDTO result) {
            return PointHistoryDTO.builder()
                    .pointHistoryId(result.pointHistoryId())
                    .amount(result.amount())
                    .type(result.type())
                    .build();
        }
    }

    @Builder
    public record searchPointResponseDTO(
            Long userId,
            String userName,
            Long point
    ) {
        public static searchPointResponseDTO from(UserResult.searchPoint result){
            return searchPointResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .build();
        }
    }

    @Builder
    public record chargePointResponseDTO(
            Long userId,
            String userName,
            Long point
    ) {
        public static chargePointResponseDTO from(UserResult.chargePoint result){
            return chargePointResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .build();
        }
    }

    @Builder
    public record historyResponseDTO(
            Long userId,
            String userName,
            Long point,
            List<PointHistoryDTO> pointHistory
    ) {
        public static historyResponseDTO from(UserResult.history result){
            return historyResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .pointHistory(result.pointHistory().stream()
                            .map(PointHistoryDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Builder
    public record searchPointRequestDTO(
            @PathVariable long userId
    ) {
        public UserCommand.searchPoint toCommand() {
            return UserCommand.searchPoint.builder()
                    .userId(userId)
                    .build();
        }
    }

    @Builder
    public record chargePointRequestDTO(
            @PathVariable long userId,
            @RequestBody chargePointRequestBody chargePointRequestBody
    ) {
        public UserCommand.chargePoint toCommand() {
            return UserCommand.chargePoint.builder()
                    .userId(userId)
                    .amount(chargePointRequestBody.point())
                    .build();
        }
    }

    public record chargePointRequestBody(
            Long point
    ) {}

    @Builder
    public record historyRequestDTO(
            @PathVariable long userId
    ) {
        public UserCommand.history toCommand() {
            return UserCommand.history.builder()
                    .userId(userId)
                    .build();
        }
    }
}
