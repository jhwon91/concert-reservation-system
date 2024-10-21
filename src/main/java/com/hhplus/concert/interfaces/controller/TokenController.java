package com.hhplus.concert.interfaces.controller;

import com.hhplus.concert.application.TokenFacade;
import com.hhplus.concert.interfaces.dto.TokenRequestDTO;
import com.hhplus.concert.interfaces.dto.TokenResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenFacade tokenFacade;

    public TokenController(TokenFacade tokenFacade) {
        this.tokenFacade = tokenFacade;
    }


    @PostMapping("/tokens")
    public TokenResponseDTO issueToken(@RequestBody TokenRequestDTO request) {
//        request.toCommand()
        return new TokenResponseDTO(UUID.randomUUID().toString(), "WAIT", 5);
    }
}
