/**
 * 시나리오 3: 좌석 동시성 테스트
 *
 * 목적: 동일 좌석에 N명이 동시 예약 시도 → 낙관적 잠금(@Version) 검증
 *       단 1명만 성공해야 하고, 나머지는 409/500 반환해야 함
 *
 * 실행:
 *   k6 run k6/scenarios/seat-concurrency.js
 *   k6 run --env VUS=200 k6/scenarios/seat-concurrency.js
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';
import { BASE_URL, SEED, VUS } from '../config.js';
import { issueToken, waitUntilActive } from '../helpers/api.js';

const reservationSuccess = new Counter('reservation_success_total');
const reservationFail    = new Counter('reservation_fail_total');
const conflictRate       = new Rate('optimistic_lock_conflict_rate');

export const options = {
  // 모든 VU가 동시에 같은 좌석을 예약하도록 ramping 없이 바로 풀 VU
  scenarios: {
    seat_rush: {
      executor: 'shared-iterations',
      vus: VUS.seatConcurrency,
      iterations: VUS.seatConcurrency,  // VU당 1번만 실행
      maxDuration: '3m',
    },
  },
  thresholds: {
    // 예약 성공은 반드시 1건이어야 함 (낙관적 잠금 검증)
    'reservation_success_total': [{ threshold: 'count<=1', abortOnFail: false }],
  },
};

// setup: 모든 VU가 사용할 토큰을 미리 발급 (단, k6 setup은 공유 불가 → default에서 처리)
export default function () {
  const userId = SEED.userIds[__VU % SEED.userIds.length];

  // 1. 토큰 발급
  const token = issueToken(userId);
  if (!token) return;

  // 2. ACTIVE 대기 (폴링)
  let activated = false;
  for (let i = 0; i < 30; i++) {
    sleep(2);
    const res = http.get(`${BASE_URL}/api/v1/queue/status?token=${token}`);
    if (res.status === 200 && res.json('data.status') === 'ACTIVE') {
      activated = true;
      break;
    }
  }
  if (!activated) return;

  // 3. 동일 좌석 예약 시도 (hotSeatId로 전체 VU가 동시 요청)
  const res = http.post(
    `${BASE_URL}/api/v1/reservations`,
    JSON.stringify({ userId, seatId: SEED.hotSeatId }),
    { headers: { 'Content-Type': 'application/json', 'X-Queue-Token': token } }
  );

  if (res.status === 200) {
    reservationSuccess.add(1);
    conflictRate.add(0);

    // 성공한 VU만 결제 진행
    const reservationId = res.json('data.reservationId');
    sleep(1);
    http.post(
      `${BASE_URL}/api/v1/payments`,
      JSON.stringify({ userId, reservationId, paymentMethod: 'POINT' }),
      { headers: { 'Content-Type': 'application/json', 'X-Queue-Token': token } }
    );
  } else {
    reservationFail.add(1);
    conflictRate.add(1);
    check(res, {
      '충돌 시 409 또는 500': (r) => r.status === 409 || r.status === 500,
    });
  }
}
