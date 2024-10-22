package com.hhplus.concert.interfaces.controller;


import com.hhplus.concert.interfaces.dto.BalanceChargeRequestDTO;
import com.hhplus.concert.interfaces.dto.UserBalanceResponseDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/balance")
public class UserController {
    /**
     * 6. 사용자 잔액 조회
     */
    @PostMapping("/{userId}")
    public UserBalanceResponseDTO pointSearch(@PathVariable long userId) {
        return null;
    }

    /**
     * 7. 잔액 충전
     */
    @PostMapping("{userId}")
    public UserBalanceResponseDTO chargePoint(@PathVariable long userId, @RequestBody BalanceChargeRequestDTO requestDTO) {
        return null;
    }

    /**
     * 특정 유저의 잔액 충전/이용 내역을 조회하는 기능
     */
    @GetMapping("/histories/{userId}")
    public UserBalanceResponseDTO history (@PathVariable long userId) {
        return null;
    }
}
