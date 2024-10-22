package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.TokenFacade;
import com.hhplus.concert.interfaces.dto.TokenRequestDTO;
import com.hhplus.concert.interfaces.dto.TokenResponseDTO;
import com.hhplus.concert.interfaces.dto.TokenStatusResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenFacade tokenFacade;

    public TokenController(TokenFacade tokenFacade) {
        this.tokenFacade = tokenFacade;
    }

    /**
     * 1. 토큰 발급
     */
    @PostMapping("/tokens")
    public TokenResponseDTO issueToken(@RequestBody TokenRequestDTO request) {
//        request.toCommand()
        return new TokenResponseDTO(UUID.randomUUID().toString(), "WAIT", 5);
    }

    /**
     * 2. 대기열 상태 확인
     */
    @GetMapping("/tokens/status")
    public TokenStatusResponseDTO getTokenStatus(@RequestHeader("Authorization") String Token) {
        // 간단한 Mock 데이터 반환
        return new TokenStatusResponseDTO("WAIT", 5);
    }

}
