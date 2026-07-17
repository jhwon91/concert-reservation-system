# 콘서트 예약 시스템

대기열 토큰 기반의 콘서트 좌석 예약 서비스입니다.
토큰을 발급받은 사용자만 좌석 예약 및 결제가 가능하며, 좌석 예약 후 5분 내 결제하지 않으면 임시 배정이 자동 해제됩니다.

---

## 기술 스택

**Language:** Java 17

**Framework:** Spring Boot 3.3.4

**Database:** PostgreSQL · H2 (테스트)

**ORM / Migration:** JPA / Hibernate · Flyway

**Test:** JUnit 5 · Mockito · K6 (부하 테스트)

**DevOps:** Docker Compose · Swagger (springdoc-openapi)

---

## 핵심 기능

### 1. DDD 기반 레이어드 아키텍처

도메인(Bounded Context)을 최상위 패키지로, 그 안에 4개 레이어를 배치합니다.
의존성은 항상 Domain을 향하며, Domain은 외부에 전혀 의존하지 않습니다.

```
Presentation → Application → Domain ← Infrastructure
```

Repository 인터페이스를 Domain에 선언하고 Infrastructure에서 구현하는 **의존성 역전(DIP)** 을 적용해 Domain이 JPA와 DB를 모르게 설계했습니다.

### 2. 대기열 토큰 시스템

사용자가 토큰을 발급받으면 `WAITING` 상태로 대기열에 진입합니다.
스케줄러가 주기적으로 앞 순번 토큰을 `ACTIVE`로 전환하고, 결제 완료 또는 만료 시 `EXPIRED`로 처리합니다.

```
WAITING ──(스케줄러)──▶ ACTIVE ──(결제완료)──▶ EXPIRED
```

`X-Queue-Token` 헤더로 토큰을 전달하면 Filter에서 ACTIVE 여부를 검증합니다.

### 3. 좌석 임시 점유 및 자동 만료

좌석 예약 요청 시 5분간 임시 점유(`RESERVED`)가 생성됩니다.
`ReservationScheduler`가 만료된 예약을 주기적으로 탐지해 좌석을 `AVAILABLE`로 복구합니다.

```
AVAILABLE ──(예약)──▶ RESERVED ──(결제완료)──▶ OCCUPIED
                          │
                          └──(만료/취소)──▶ AVAILABLE
```

### 4. 동시성 제어

동시에 여러 사용자가 같은 좌석을 예약하거나 포인트를 사용할 때 데이터 정합성을 보장합니다.

| 상황 | 전략 | 적용 위치 |
|------|------|-----------|
| 좌석 예약 | 낙관적 잠금 (`@Version`) | `Seat.version` |
| 포인트 차감 | 비관적 잠금 (`PESSIMISTIC_WRITE`) | `UserPointRepository.findByUserIdWithLock()` |

### 5. 스케줄러

| 스케줄러 | 역할 |
|---------|------|
| `QueueScheduler` | WAITING 토큰 → ACTIVE 전환, 만료 토큰 EXPIRED 처리 |
| `ReservationScheduler` | 5분 경과한 PENDING 예약 CANCELLED 처리 및 좌석 반환 |

---

## 도메인 구성

| Aggregate Root | 포함 Entity | 비고 |
|----------------|-------------|------|
| User | - | 사용자 기본 정보 |
| UserPoint | PointTransaction | 잔액 관리, `@Version` 낙관적 잠금 |
| QueueToken | - | 대기열 토큰 (WAITING / ACTIVE / EXPIRED) |
| Concert | ConcertDate | ConcertDate는 Concert 내부 Entity |
| Seat | - | `@Version` 낙관적 잠금 (AVAILABLE / RESERVED / OCCUPIED) |
| Reservation | - | 예약 (PENDING / CONFIRMED / CANCELLED), +5분 만료 |
| Payment | - | 결제 (PENDING / COMPLETED / FAILED) |

---

## API 엔드포인트

| Method | URL | 설명 | 토큰 필요 |
|--------|-----|------|-----------|
| `POST` | `/api/v1/queue/tokens` | 대기열 토큰 발급 | X |
| `GET` | `/api/v1/queue/status` | 대기 순번 조회 | X |
| `GET` | `/api/v1/concerts/dates` | 예약 가능 날짜 조회 | X |
| `GET` | `/api/v1/concerts/seats` | 예약 가능 좌석 조회 | X |
| `POST` | `/api/v1/reservations` | 좌석 예약 (5분 임시 점유) | O |
| `GET` | `/api/v1/users/{userId}/point` | 잔액 조회 | X |
| `PATCH` | `/api/v1/users/{userId}/point` | 잔액 충전 | X |
| `POST` | `/api/v1/payments` | 결제 (포인트 차감 → 예약 확정) | O |

> 토큰이 필요한 API는 `X-Queue-Token` 헤더에 ACTIVE 상태의 토큰 값을 전달해야 합니다.

---

## 문서

| 문서 | 링크 |
|------|------|
| 아키텍처 설계 | [docs/01_Architecture.md](docs/01_Architecture.md) |
| 시퀀스 다이어그램 | [docs/02_SequenceDiagram.md](docs/02_SequenceDiagram.md) |
| ERD | [docs/03.ERD.md](docs/03.ERD.md) |
| API 명세서 | [docs/04.API.md](docs/04.API.md) |

---

## 실행 방법

```bash
# PostgreSQL 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'

# 테스트
./gradlew test

# 빌드
./gradlew build
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 인프라 확장 계획

```
1단계 (현재)  PostgreSQL → 기능 구현 완성 + K6 성능 기준치 측정
2단계         Redis 대기열 (Sorted Set) 적용 → 성능 비교 보고서
3단계         Kafka 이벤트 발행 (결제 완료 이벤트) → 성능 비교 보고서
```
