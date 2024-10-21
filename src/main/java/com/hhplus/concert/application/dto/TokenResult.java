package com.hhplus.concert.application.dto;
import com.hhplus.concert.interfaces.dto.TokenResponseDTO;

public record TokenResult(
        String token,
        String status,
        Integer position
) {
    public static TokenResult of (TokenResponseDTO responseDTO) {
        return new TokenResult(
                responseDTO.token(),
                responseDTO.status(),
                responseDTO.position()
        );
    }
}



