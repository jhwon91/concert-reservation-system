package com.hhplus.concert.user.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "point_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction extends BaseEntity {

    @Id
    @Column(name = "point_transaction_id")
    private UUID pointTransactionId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type;

    @Column(name = "related_id")
    private UUID relatedId;

    public PointTransaction(UUID userId, BigDecimal amount, BigDecimal balanceAfter, PointTransactionType type) {
        this.pointTransactionId = UUID.randomUUID();
        this.userId = userId;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.type = type;
    }

    public PointTransaction(UUID userId, BigDecimal amount, BigDecimal balanceAfter, PointTransactionType type, UUID relatedId) {
        this(userId, amount, balanceAfter, type);
        this.relatedId = relatedId;
    }
}
