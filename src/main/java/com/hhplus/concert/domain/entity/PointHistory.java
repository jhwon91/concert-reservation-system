package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.enums.TransactionType;
import jakarta.persistence.*;

@Entity
@Table(name = "point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private Long amount;

    @Enumerated(EnumType.STRING)
    TransactionType type;

}