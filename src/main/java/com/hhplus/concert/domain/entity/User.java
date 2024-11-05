package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long point;

    @Version
    @Builder.Default
    private Long version = 0L;

    public User chargePoint(long amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.INVALID_CHARGE_AMOUNT, amount);
        }
        this.point += amount;
        return this;
    }
}
