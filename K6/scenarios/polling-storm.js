/**
 * 시나리오 2: 순번 조회 폭탄 테스트
 *
 * 목적: WAITING 상태 유저 N명이 동시에 폴링할 때 DB/서버가 버티는지 측정
 *       Redis 도입 전후 성능 비교용 기준치
 *
 * 실행:
 *   k6 run k6/scenarios/polling-storm.js
 *   k6 run --env VUS=10000 k6/scenarios/polling-storm.js
 */

import { sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import { SEED, VUS, THRESHOLDS } from '../config.js';
import { issueToken, getQueueStatus } from '../helpers/api.js';

const pollErrorRate    = new Rate('poll_error_rate');
const pollDurationTrend = new Trend('poll_duration_ms');

export const options = {
  stages: [
    { duration: '10s', target: Math.floor(VUS.pollingStorm * 0.2) },
    { duration: '30s', target: VUS.pollingStorm },   // 목표 동시 폴링 수
    { duration: '20s', target: VUS.pollingStorm },   // 유지
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    ...THRESHOLDS,
    'poll_duration_ms':  [{ threshold: 'p(95)<500', abortOnFail: false }],  // 순번 조회 p95 < 500ms
    'poll_error_rate':   [{ threshold: 'rate<0.01', abortOnFail: false }],
  },
};

// setup: VU마다 setup에서 토큰 발급하면 좋지만 k6 setup은 단일 실행
// → default function 진입 시 토큰 발급 후 폴링 반복
export default function () {
  const userId = SEED.userIds[__VU % SEED.userIds.length];

  // 토큰 발급 (VU 최초 1회)
  const token = issueToken(userId);
  if (!token) {
    pollErrorRate.add(1);
    return;
  }

  // 폴링 반복 (1초 간격, 30회)
  for (let i = 0; i < 30; i++) {
    const start = Date.now();
    const status = getQueueStatus(token);
    const elapsed = Date.now() - start;

    pollDurationTrend.add(elapsed);
    pollErrorRate.add(status === null ? 1 : 0);

    if (status && (status.status === 'ACTIVE' || status.status === 'EXPIRED')) break;

    sleep(1);
  }
}
