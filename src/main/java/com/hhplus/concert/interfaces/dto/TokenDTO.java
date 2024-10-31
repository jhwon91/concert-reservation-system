package com.hhplus.concert.interfaces.dto;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.hhplus.concert.application.dto.TokenCommand;
import com.hhplus.concert.application.dto.TokenResult;
import com.hhplus.concert.domain.enums.TokenStatus;
import lombok.Builder;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public class TokenDTO {

    @Builder
    public record TokenResponseDTO(
            UUID token,
            TokenStatus status,
            Integer position
    ) {
        public static TokenResponseDTO from(TokenResult.issueToken result){
            return TokenResponseDTO.builder()
                    .token(result.token())
                    .status(result.status())
                    .position(result.position())
                    .build();
        }
    }

    @Builder
    public record TokenStatusResponseDTO(
            TokenStatus status,
            Integer position
    ) {
        public static TokenStatusResponseDTO from(TokenResult.tokenStatus result){
            return TokenStatusResponseDTO.builder()
                    .status(result.status())
                    .position(result.position())
                    .build();
        }
    }

    @Builder
    public record issueTokenRequestDTO(
            Long user_id
    ) {
        public TokenCommand.issueTokenCommand toCommand() {
            return TokenCommand.issueTokenCommand.builder()
                    .userId(user_id)
                    .build();
        }
    }

    @Builder
    public record tokenStatusRequestDTO(
            @RequestHeader("Authorization") UUID token
    ) {
        public TokenCommand.tokenStatusCommand toCommand() {
            return TokenCommand.tokenStatusCommand.builder()
                    .token(token)
                    .build();
        }
    }
}
