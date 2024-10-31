package com.hhplus.concert.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_detail_id")
    private Long concertDetailId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "seat_id")
    private Long seatId;

    private String status;

    @Column(name = "reservation_at")
    private LocalDateTime reservationAt;

    public static Reservation create(User user, ConcertDetails concertDetails, Seat seat) {
        return Reservation.builder()
                .concertDetailId(concertDetails.getId())
                .userId(user.getId())
                .seatId(seat.getId())
                .status(null) // 필요한 경우 초기 상태 설정
                .reservationAt(LocalDateTime.now())
                .build();
    }
}
