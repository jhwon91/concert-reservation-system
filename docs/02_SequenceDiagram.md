# 시퀀스 다이어그램

## 1. 유저 토큰 발급

- 토큰은 항상 WAITING으로 발급
- 스케줄러가 주기적으로 WAITING → ACTIVE 전환 (최대 동시 100명)

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Filter as QueueTokenFilter
    participant Controller as QueueController
    participant Service as IssueQueueTokenService
    participant Domain as QueueDomainService
    participant DB as Database

    Client->>Controller: POST /api/v1/queue/tokens
    Controller->>Service: execute(IssueQueueTokenCommand)
    Service->>DB: 사용자 존재 확인
    DB-->>Service: User
    Service->>DB: 현재 WAITING 토큰 수 조회
    DB-->>Service: currentQueueSize
    Service->>Domain: issueToken(userId, currentQueueSize)
    Domain-->>Service: QueueToken (WAITING, position, estimatedWaitSec)
    Service->>DB: QueueToken 저장
    DB-->>Service: 저장 완료
    Service-->>Controller: QueueTokenResponse
    Controller-->>Client: 200 OK (tokenValue, position, estimatedWaitSec, status: WAITING)
```

---

## 2. 대기열 상태 조회 (폴링)

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Controller as QueueController
    participant Service as GetQueueStatusService
    participant DB as Database

    loop 폴링 (주기적 호출)
        Client->>Controller: GET /api/v1/queue/status?token={tokenValue}
        Controller->>Service: execute(GetQueueStatusQuery)
        Service->>DB: tokenValue로 QueueToken 조회
        DB-->>Service: QueueToken

        alt WAITING
            Service-->>Controller: position, estimatedWaitSec, status: WAITING
            Controller-->>Client: 200 OK (대기 중)
        else ACTIVE
            Service-->>Controller: position: 0, status: ACTIVE
            Controller-->>Client: 200 OK (서비스 이용 가능)
        else EXPIRED
            Service-->>Controller: status: EXPIRED
            Controller-->>Client: 200 OK (토큰 만료)
        end
    end
```

---

## 3. 대기열 스케줄러 (자동 활성화 / 만료)

```mermaid
sequenceDiagram
    participant Scheduler as QueueScheduler
    participant DB as Database

    loop 10초마다 (활성화)
        Scheduler->>DB: ACTIVE 토큰 수 조회
        DB-->>Scheduler: activeCount
        Scheduler->>DB: WAITING 토큰 조회 (position 순, limit: 100 - activeCount)
        DB-->>Scheduler: waitingTokens
        loop 각 토큰
            Scheduler->>DB: QueueToken.activate() 저장 (WAITING → ACTIVE)
        end
    end

    loop 60초마다 (만료)
        Scheduler->>DB: expires_at < now() 인 토큰 조회
        DB-->>Scheduler: expiredTokens
        loop 각 토큰
            Scheduler->>DB: QueueToken.expire() 저장 (→ EXPIRED)
        end
    end
```

---

## 4. 예약 가능 날짜 조회

- 토큰 불필요

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Controller as ConcertController
    participant Service as GetAvailableDatesService
    participant DB as Database

    Client->>Controller: GET /api/v1/concerts/{concertId}/dates
    Controller->>Service: execute(GetAvailableDatesQuery)
    Service->>DB: 오늘 이후 ConcertDate 조회 (availableSeats > 0)
    DB-->>Service: List<ConcertDate>
    Service-->>Controller: List<ConcertDateResponse>
    Controller-->>Client: 200 OK
```

---

## 5. 예약 가능 좌석 조회

- 토큰 불필요

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Controller as ConcertController
    participant Service as GetAvailableSeatsService
    participant DB as Database

    Client->>Controller: GET /api/v1/concerts/dates/{concertDateId}/seats
    Controller->>Service: execute(GetAvailableSeatsQuery)
    Service->>DB: AVAILABLE 상태 Seat 조회
    DB-->>Service: List<Seat>
    Service-->>Controller: List<SeatResponse>
    Controller-->>Client: 200 OK (seatNumber, price, status)
```

---

## 6. 좌석 예약 요청

- `X-Queue-Token` 헤더 필요 (ACTIVE 상태)
- 좌석 임시 점유 5분, 스케줄러가 만료 처리

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Filter as QueueTokenValidationFilter
    participant Controller as ReservationController
    participant Service as CreateReservationService
    participant Domain as ConcertDomainService
    participant DB as Database

    Client->>Filter: POST /api/v1/reservations (X-Queue-Token 헤더)
    Filter->>DB: tokenValue로 QueueToken 조회
    DB-->>Filter: QueueToken

    alt 토큰 ACTIVE
        Filter->>Controller: 요청 전달
        Controller->>Service: execute(CreateReservationCommand)
        Service->>DB: Seat 조회 (@Version 낙관적 잠금)
        DB-->>Service: Seat

        alt 좌석 AVAILABLE
            Service->>Domain: validateSeatAvailable(seat)
            Service->>DB: Seat.reserve() 저장 (AVAILABLE → RESERVED)
            Service->>DB: Reservation 저장 (PENDING, expiresAt = now + 5분)
            Service-->>Controller: ReservationResponse
            Controller-->>Client: 200 OK (reservationId, expiresAt)
        else 좌석 선점됨
            Service-->>Client: 409 Conflict (이미 예약된 좌석)
        end
    else 토큰 없음/비활성
        Filter-->>Client: 401 Unauthorized
    end
```

---

## 7. 예약 만료 스케줄러

```mermaid
sequenceDiagram
    participant Scheduler as ReservationScheduler
    participant DB as Database

    loop 30초마다
        Scheduler->>DB: PENDING + expires_at < now() 예약 조회
        DB-->>Scheduler: expiredReservations
        loop 각 예약
            Scheduler->>DB: Reservation.cancel() 저장 (→ CANCELLED)
            Scheduler->>DB: Seat.release() 저장 (→ AVAILABLE)
        end
    end
```

---

## 8. 잔액 충전

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Controller as UserController
    participant Service as ChargePointService
    participant Domain as PointDomainService
    participant DB as Database

    Client->>Controller: PATCH /api/v1/users/{userId}/point
    Controller->>Service: execute(ChargePointCommand)
    Service->>DB: UserPoint 비관적 잠금 조회
    DB-->>Service: UserPoint
    Service->>Domain: chargePoint(userPoint, amount)
    Domain->>Domain: 금액 검증 (최소 1,000 / 최대 1,000,000 / 잔액 최대 10,000,000)
    Domain-->>Service: PointTransaction
    Service->>DB: UserPoint 저장 (잔액 업데이트)
    Service->>DB: PointTransaction 저장
    Service-->>Controller: UserPointResponse
    Controller-->>Client: 200 OK (balance)
```

---

## 9. 잔액 조회

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Controller as UserController
    participant Service as GetUserPointService
    participant DB as Database

    Client->>Controller: GET /api/v1/users/{userId}/point
    Controller->>Service: execute(GetUserPointQuery)
    Service->>DB: UserPoint 조회
    DB-->>Service: UserPoint
    Service-->>Controller: UserPointResponse
    Controller-->>Client: 200 OK (balance)
```

---

## 10. 결제

- `X-Queue-Token` 헤더 필요 (ACTIVE 상태)
- 포인트 차감 → 예약 확정 → 좌석 확정 → 토큰 만료

```mermaid
sequenceDiagram
    participant Client as 사용자
    participant Filter as QueueTokenValidationFilter
    participant Controller as PaymentController
    participant Service as ProcessPaymentService
    participant PayDomain as PaymentDomainService
    participant PointDomain as PointDomainService
    participant DB as Database

    Client->>Filter: POST /api/v1/payments (X-Queue-Token 헤더)
    Filter->>DB: QueueToken 조회 및 ACTIVE 검증
    Filter->>Controller: 요청 전달

    Controller->>Service: execute(ProcessPaymentCommand)

    Service->>DB: Reservation 조회
    DB-->>Service: Reservation (PENDING 검증, 만료 검증)

    Service->>DB: UserPoint 비관적 잠금 조회
    DB-->>Service: UserPoint

    Service->>PayDomain: createPayment(reservation, user, POINT)
    PayDomain-->>Service: Payment (PENDING)
    Service->>DB: Payment 저장

    Service->>PayDomain: processPointPayment(payment, userPoint)
    PayDomain->>PointDomain: usePoint(userPoint, amount)
    PointDomain->>PointDomain: 잔액 검증
    PointDomain-->>PayDomain: PointTransaction
    PayDomain-->>Service: transactionId

    Service->>DB: UserPoint 저장 (잔액 차감)
    Service->>DB: PointTransaction 저장

    Service->>DB: Reservation.confirm() 저장 (PENDING → CONFIRMED)
    Service->>DB: Seat.occupy() 저장 (RESERVED → OCCUPIED)
    Service->>DB: QueueToken.expire() 저장 (ACTIVE → EXPIRED)
    Service->>DB: Payment.complete() 저장 (PENDING → COMPLETED)

    Service-->>Controller: PaymentResponse
    Controller-->>Client: 200 OK (paymentId, amount, status: COMPLETED)
```
