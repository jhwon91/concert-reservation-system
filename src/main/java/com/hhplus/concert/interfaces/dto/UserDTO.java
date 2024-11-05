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
    public record SearchPointResponseDTO(
            Long userId,
            String userName,
            Long point
    ) {
        public static SearchPointResponseDTO from(UserResult.SearchPoint result){
            return SearchPointResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .build();
        }
    }

    @Builder
    public record ChargePointResponseDTO(
            Long userId,
            String userName,
            Long point
    ) {
        public static ChargePointResponseDTO from(UserResult.ChargePoint result){
            return ChargePointResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .build();
        }
    }

    @Builder
    public record HistoryResponseDTO(
            Long userId,
            String userName,
            Long point,
            List<PointHistoryDTO> pointHistory
    ) {
        public static HistoryResponseDTO from(UserResult.History result){
            return HistoryResponseDTO.builder()
                    .userId(result.userId())
                    .userName(result.userName())
                    .point(result.point())
                    .pointHistory(result.pointHistory().stream()
                            .map(PointHistoryDTO::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    public record SearchPointRequestDTO(
            @PathVariable long userId
    ) {
        public UserCommand.SearchPoint toCommand() {
            return UserCommand.SearchPoint.builder()
                    .userId(userId)
                    .build();
        }
    }

    public record ChargePointRequestDTO(
            @PathVariable long userId,
            @RequestBody ChargePointRequestBody chargePointRequestBody
    ) {
        public UserCommand.ChargePoint toCommand() {
            return UserCommand.ChargePoint.builder()
                    .userId(userId)
                    .amount(chargePointRequestBody.point())
                    .build();
        }
    }

    public record ChargePointRequestBody(
            Long point
    ) {}

    public record HistoryRequestDTO(
            @PathVariable long userId
    ) {
        public UserCommand.History toCommand() {
            return UserCommand.History.builder()
                    .userId(userId)
                    .build();
        }
    }
}
