package com.hhplus.concert.interfaces.controller;


import com.hhplus.concert.application.UserFacade;
import com.hhplus.concert.interfaces.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/balance")
public class UserController {

    private final UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * 6. 사용자 잔액 조회
     */
    @GetMapping("/{userId}")
    public UserDTO.SearchPointResponseDTO SearchPoint(UserDTO.SearchPointRequestDTO request) {
        return UserDTO.SearchPointResponseDTO.from(userFacade.searchPoint(request.toCommand()));
    }

    /**
     * 7. 잔액 충전
     */
    @PostMapping("{userId}")
    public UserDTO.ChargePointResponseDTO chargePoint(UserDTO.ChargePointRequestDTO request) {
        return UserDTO.ChargePointResponseDTO.from(userFacade.chargePoint(request.toCommand()));
    }

    /**
     * 특정 유저의 잔액 충전/이용 내역을 조회하는 기능
     */
    @GetMapping("/histories/{userId}")
    public UserDTO.HistoryResponseDTO history (UserDTO.HistoryRequestDTO request) {
        return UserDTO.HistoryResponseDTO.from(userFacade.history(request.toCommand()));
    }
}
