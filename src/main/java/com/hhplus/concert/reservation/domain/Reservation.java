package com.hhplus.concert.reservation.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    private static final int EXPIRY_MINUTES = 5;

    @Id
    @Column(name = "reservation_id")
    private UUID reservationId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID seatId;

    @Column(nullable = false)
    private UUID concertDateId;

    @Column(nullable = false)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    private LocalDateTime confirmedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public Reservation(UUID userId, UUID seatId, UUID concertDateId, String tokenValue) {
        this.reservationId = UUID.randomUUID();
        this.userId = userId;
        this.seatId = seatId;
        this.concertDateId = concertDateId;
        this.tokenValue = tokenValue;
        this.status = ReservationStatus.PENDING;
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = this.reservedAt.plusMinutes(EXPIRY_MINUTES);
    }

    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new CoreException(ErrorType.RESERVATION_NOT_PENDING, this.status);
        }
        if (isExpired()) {
            throw new CoreException(ErrorType.RESERVATION_EXPIRED, this.expiresAt);
        }
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELLED) {
            throw new CoreException(ErrorType.RESERVATION_ALREADY_CANCELLED, this.status);
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
