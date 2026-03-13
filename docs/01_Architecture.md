# 아키텍처 설계

## 의존 방향

모든 의존성 화살표는 항상 **Domain을 향한다.**
Domain은 아무것도 의존하지 않는다.

```
┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Presentation │──▶│ Application  │──▶│    Domain    │◀──│Infrastructure│
│              │   │              │   │              │   │              │
│ Controller   │   │ UseCase      │   │ Aggregate    │   │ JPA Repo     │
│ Filter       │   │ Service      │   │ DomainService│   │ Adapter      │
│ Request/Resp │   │ Command/Query│   │ VO           │   │ Converter    │
│              │   │              │   │ Repo(인터페이스)│   │              │
└──────────────┘   └──────────────┘   └──────────────┘   └──────────────┘
```

### 의존성 역전 (DIP)

Infrastructure가 Domain을 바라보는 핵심 원리는 **의존성 역전**이다.
Domain에 Repository **인터페이스**를 선언하고, Infrastructure에서 이를 **구현**한다.
덕분에 Domain은 JPA, DB를 전혀 모른다.

```
domain/user/repository/UserRepository.java         ← 인터페이스 (Domain)
infrastructure/persistence/adapter/
    UserRepositoryAdapter.java                      ← 구현체 (Infrastructure)
        implements UserRepository
```

### 계층 규칙 요약

| 계층 | 의존 가능 | 의존 불가 |
|------|-----------|-----------|
| presentation | application | domain, infrastructure |
| application | domain (인터페이스) | infrastructure |
| domain | 없음 (순수 Java) | 모든 외부 |
| infrastructure | domain (인터페이스 구현) | application, presentation |

---

## DDD 설계

### Aggregate 목록

| Aggregate Root | 포함 | 비고 |
|----------------|------|------|
| User | - | 사용자 기본 정보 |
| UserPoint | PointTransaction | @Version 낙관적 잠금 |
| QueueToken | - | 대기열 토큰 |
| Concert | ConcertDate | ConcertDate는 Concert 내부 Entity |
| Seat | - | @Version 낙관적 잠금, Concert와 분리 |
| Reservation | - | 예약 (5분 만료) |
| Payment | - | 결제 |

### 상태 전이

```
QueueToken
  WAITING ──(스케줄러)──▶ ACTIVE ──(결제완료)──▶ EXPIRED

Seat
  AVAILABLE ──(예약)──▶ RESERVED ──(결제완료)──▶ OCCUPIED
       ▲                    │
       └──(만료/취소)────────┘

Reservation
  PENDING ──(결제완료)──▶ CONFIRMED
     │
     └──(만료/취소)──▶ CANCELLED

Payment
  PENDING ──(성공)──▶ COMPLETED
     │
     └──(실패)──▶ FAILED
```

---

## 동시성 전략

| 상황 | 전략 | 적용 위치 |
|------|------|-----------|
| 좌석 예약 | 낙관적 잠금 (`@Version`) | `Seat.version` |
| 포인트 차감 | 비관적 잠금 (`PESSIMISTIC_WRITE`) | `UserPointRepository.findByUserIdWithLock()` |

---

## 인프라 확장 계획

```
1단계 (현재)
  └── PostgreSQL만으로 기능 구현
  └── 성능 기준치 측정 (k6 부하 테스트)

2단계
  └── Redis 대기열 (Sorted Set) 적용
  └── 성능 비교 보고서 작성

3단계
  └── Kafka 이벤트 발행 적용 (결제 완료 이벤트)
  └── 성능 비교 보고서 작성
```

---

## 패키지 구조

도메인(Bounded Context)을 최상위로, 그 안에 레이어를 배치한다.
각 도메인이 독립적으로 응집되어 나중에 MSA 분리가 용이하다.

```
com.hhplus.concert/
│
├── user/
│   ├── domain/
│   │   ├── User.java                      # AR
│   │   ├── UserPoint.java                 # AR (@Version)
│   │   ├── PointTransaction.java
│   │   ├── PointTransactionType.java      # CHARGE / USE / REFUND
│   │   ├── vo/
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── UserPointRepository.java   # findByUserIdWithLock() 포함
│   │   │   └── PointTransactionRepository.java
│   │   └── service/
│   │       └── PointDomainService.java
│   ├── application/
│   │   ├── dto/                           # ChargePointCommand, UserPointResponse ...
│   │   └── service/                       # ChargePointService, GetUserPointService
│   ├── presentation/
│   │   ├── UserController.java
│   │   └── dto/                           # ChargePointRequest, UserPointResponse
│   └── infrastructure/
│       ├── JpaUserRepository.java
│       ├── JpaUserPointRepository.java
│       ├── JpaPointTransactionRepository.java
│       └── adapter/
│           ├── UserRepositoryAdapter.java
│           ├── UserPointRepositoryAdapter.java
│           └── PointTransactionRepositoryAdapter.java
│
├── queue/
│   ├── domain/
│   │   ├── QueueToken.java                # AR
│   │   ├── QueueTokenStatus.java          # WAITING / ACTIVE / EXPIRED
│   │   ├── vo/
│   │   │   └── TokenValue.java
│   │   ├── repository/
│   │   │   └── QueueTokenRepository.java
│   │   └── service/
│   │       └── QueueDomainService.java
│   ├── application/
│   │   ├── dto/                           # IssueTokenCommand, QueueStatusQuery ...
│   │   └── service/                       # IssueQueueTokenService, GetQueueStatusService
│   ├── presentation/
│   │   ├── QueueController.java
│   │   └── dto/
│   └── infrastructure/
│       ├── JpaQueueTokenRepository.java
│       └── adapter/
│           └── QueueTokenRepositoryAdapter.java
│
├── concert/
│   ├── domain/
│   │   ├── Concert.java                   # AR
│   │   ├── ConcertDate.java               # Entity (Concert 내부)
│   │   ├── Seat.java                      # AR (@Version)
│   │   ├── SeatStatus.java                # AVAILABLE / RESERVED / OCCUPIED
│   │   ├── vo/
│   │   │   └── SeatNumber.java            # 1~50 검증
│   │   ├── repository/
│   │   │   ├── ConcertRepository.java
│   │   │   ├── ConcertDateRepository.java
│   │   │   └── SeatRepository.java
│   │   └── service/
│   │       └── ConcertDomainService.java
│   ├── application/
│   │   ├── dto/                           # GetAvailableDatesQuery, SeatResponse ...
│   │   └── service/                       # GetAvailableDatesService, GetAvailableSeatsService
│   ├── presentation/
│   │   ├── ConcertController.java
│   │   └── dto/
│   └── infrastructure/
│       ├── JpaConcertRepository.java
│       ├── JpaConcertDateRepository.java
│       ├── JpaSeatRepository.java
│       └── adapter/
│           ├── ConcertRepositoryAdapter.java
│           ├── ConcertDateRepositoryAdapter.java
│           └── SeatRepositoryAdapter.java
│
├── reservation/
│   ├── domain/
│   │   ├── Reservation.java               # AR (expiresAt = +5분)
│   │   ├── ReservationStatus.java         # PENDING / CONFIRMED / CANCELLED
│   │   ├── repository/
│   │   │   └── ReservationRepository.java
│   │   └── service/
│   │       └── ReservationDomainService.java
│   ├── application/
│   │   ├── dto/                           # CreateReservationCommand, ReservationResponse ...
│   │   └── service/                       # CreateReservationService, CancelReservationService
│   ├── presentation/
│   │   ├── ReservationController.java
│   │   └── dto/
│   └── infrastructure/
│       ├── JpaReservationRepository.java
│       └── adapter/
│           └── ReservationRepositoryAdapter.java
│
├── payment/
│   ├── domain/
│   │   ├── Payment.java                   # AR
│   │   ├── PaymentStatus.java             # PENDING / COMPLETED / FAILED
│   │   ├── PaymentMethod.java             # POINT
│   │   ├── repository/
│   │   │   └── PaymentRepository.java
│   │   └── service/
│   │       └── PaymentDomainService.java
│   ├── application/
│   │   ├── dto/                           # ProcessPaymentCommand, PaymentResponse ...
│   │   └── service/                       # ProcessPaymentService
│   ├── presentation/
│   │   ├── PaymentController.java
│   │   └── dto/
│   └── infrastructure/
│       ├── JpaPaymentRepository.java
│       └── adapter/
│           └── PaymentRepositoryAdapter.java
│
└── common/
    ├── config/
    │   └── JpaConfig.java
    ├── exception/
    │   ├── BusinessException.java
    │   └── ErrorCode.java
    ├── filter/
    │   └── QueueTokenValidationFilter.java
    ├── scheduler/
    │   ├── QueueScheduler.java            # 대기열 활성화 / 만료
    │   └── ReservationScheduler.java      # 예약 만료 처리
    ├── response/
    │   └── ApiResponse.java
    └── vo/
        └── Money.java                     # 공통 VO (여러 도메인에서 사용)
```
