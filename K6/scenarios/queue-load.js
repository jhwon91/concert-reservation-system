/**
 * 시나리오 1: 대기열 E2E 부하 테스트
 *
 * 흐름: 토큰 발급 → ACTIVE 폴링 → 좌석 조회 → 예약 → 결제
 *
 * 실행:
 *   k6 run k6/scenarios/queue-load.js
 *   k6 run --env VUS=10000 k6/scenarios/queue-load.js
 */

import { sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';
import { SEED, VUS, THRESHOLDS } from '../config.js';
import { issueToken, getQueueStatus, getAvailableSeats, reserveSeat, processPayment } from '../helpers/api.js';

// 커스텀 메트릭
const waitTimeTrend   = new Trend('queue_wait_time_seconds');  // ACTIVE까지 걸린 시간
const activatedCount  = new Counter('queue_activated_total');   // ACTIVE 도달한 VU 수
const paymentSuccess  = new Counter('payment_success_total');   // 결제 성공 수

export const options = {
  stages: [
    { duration: '30s', target: Math.floor(VUS.queueLoad * 0.1) },  // 워밍업
    { duration: '1m',  target: VUS.queueLoad },                     // 목표 VU 유지
    { duration: '30s', target: 0 },                                 // 종료
  ],
  thresholds: {
    ...THRESHOLDS,
    'queue_wait_time_seconds': [{ threshold: 'p(95)<60', abortOnFail: false }], // 95%가 60초 내 활성화
  },
};

export default function () {
  // VU별 랜덤 userId (시드 데이터 내에서)
  const userId = SEED.userIds[__VU % SEED.userIds.length];

  // 1. 토큰 발급
  const token = issueToken(userId);
  if (!token) return;

  // 2. ACTIVE 될 때까지 폴링
  const startWait = Date.now();
  let activated = false;

  for (let i = 0; i < 60; i++) {       // 최대 60번 (2초 간격 → 최대 120초)
    sleep(2);
    const status = getQueueStatus(token);
    if (!status) break;

    if (status.status === 'ACTIVE') {
      activated = true;
      break;
    }
    if (status.status === 'EXPIRED') break;
  }

  if (!activated) return;

  const waitSec = (Date.now() - startWait) / 1000;
  waitTimeTrend.add(waitSec);
  activatedCount.add(1);

  // 3. 예약 가능 좌석 조회
  const seats = getAvailableSeats(SEED.concertDateId);
  if (!seats || seats.length === 0) return;

  // 랜덤 좌석 선택
  const seat = seats[Math.floor(Math.random() * seats.length)];

  // 4. 좌석 예약
  const reservationId = reserveSeat(token, userId, seat.seatId);
  if (!reservationId) return;

  sleep(1);

  // 5. 결제
  const paymentId = processPayment(token, userId, reservationId);
  if (paymentId) {
    paymentSuccess.add(1);
  }
}
