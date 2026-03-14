package com.hhplus.concert.user.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "user_points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseEntity {

    @Id
    @Column(name = "user_point_id")
    private UUID userPointId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;

    public UserPoint(UUID userId) {
        this.userPointId = UUID.randomUUID();
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
    }

    public void charge(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.INVALID_MONEY_AMOUNT, amount);
        }
        this.balance = this.balance.add(amount);
    }

    public void use(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.INVALID_CHARGE_AMOUNT, amount);
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new CoreException(ErrorType.INSUFFICIENT_BALANCE, this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }
}
