package com.hhplus.concert.interfaces.dto;

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
        public static TokenResponseDTO from(TokenResult.IssueToken result){
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
        public static TokenStatusResponseDTO from(TokenResult.TokenStatusResult result){
            return TokenStatusResponseDTO.builder()
                    .status(result.status())
                    .position(result.position())
                    .build();
        }
    }

    public record IssueTokenRequestDTO(
            Long user_id
    ) {
        public TokenCommand.IssueToken toCommand() {
            return TokenCommand.IssueToken.builder()
                    .userId(user_id)
                    .build();
        }
    }

    public record TokenStatusRequestDTO(
            @RequestHeader("Authorization") UUID token
    ) {
        public TokenCommand.TokenStatus toCommand() {
            return TokenCommand.TokenStatus.builder()
                    .token(token)
                    .build();
        }
    }
}
