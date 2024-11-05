package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.TokenFacade;
import com.hhplus.concert.interfaces.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenFacade tokenFacade;

    @Autowired
    public TokenController(TokenFacade tokenFacade) {
        this.tokenFacade = tokenFacade;
    }

    /**
     * 1. 토큰 발급
     */
    @PostMapping("/tokens")
    public TokenDTO.TokenResponseDTO issueToken(@RequestBody TokenDTO.IssueTokenRequestDTO request) {
        return TokenDTO.TokenResponseDTO.from(tokenFacade.issueToken(request.toCommand()));
    }

    /**
     * 2. 대기열 상태 확인
     */
    @GetMapping("/tokens/status")
    public TokenDTO.TokenStatusResponseDTO getTokenStatus(TokenDTO.TokenStatusRequestDTO request) {
        return TokenDTO.TokenStatusResponseDTO.from(tokenFacade.getTokenStatus(request.toCommand()));
    }

}
