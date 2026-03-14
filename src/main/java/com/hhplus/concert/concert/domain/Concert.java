package com.hhplus.concert.concert.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {

    @Id
    @Column(name = "concert_id")
    private UUID concertId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String venue;

    private String description;

    public Concert(String name, String venue, String description) {
        this.concertId = UUID.randomUUID();
        this.name = name;
        this.venue = venue;
        this.description = description;
    }
}
