import http from 'k6/http';
import { check } from 'k6';
import { BASE_URL } from '../config.js';

const JSON_HEADERS = { 'Content-Type': 'application/json' };

function tokenHeaders(token) {
  return { 'Content-Type': 'application/json', 'X-Queue-Token': token };
}

// 토큰 발급 → tokenValue 반환 (실패 시 null)
export function issueToken(userId) {
  const res = http.post(
    `${BASE_URL}/api/v1/queue/tokens`,
    JSON.stringify({ userId }),
    { headers: JSON_HEADERS }
  );
  check(res, { '토큰 발급 200': (r) => r.status === 200 });
  return res.status === 200 ? res.json('data.tokenValue') : null;
}

// 대기 순번 조회 → { status, position } 반환
export function getQueueStatus(token) {
  const res = http.get(`${BASE_URL}/api/v1/queue/status?token=${token}`);
  if (res.status !== 200) return null;
  const data = res.json('data');
  return { status: data.status, position: data.position };
}

// ACTIVE 될 때까지 폴링 (최대 maxWaitSec초, intervalSec마다 확인)
// 반환: true(ACTIVE됨) / false(타임아웃)
export function waitUntilActive(token, maxWaitSec = 120, intervalSec = 2) {
  const deadline = Date.now() + maxWaitSec * 1000;
  while (Date.now() < deadline) {
    const status = getQueueStatus(token);
    if (status && status.status === 'ACTIVE') return true;
    if (status && status.status === 'EXPIRED') return false;
    // k6에서 sleep 대신 busy-wait는 금지 → import sleep 사용 필요
    // 호출부에서 sleep 처리
    return 'WAITING';
  }
  return false;
}

// 좌석 예약 → reservationId 반환 (실패 시 null)
export function reserveSeat(token, userId, seatId) {
  const res = http.post(
    `${BASE_URL}/api/v1/reservations`,
    JSON.stringify({ userId, seatId }),
    { headers: tokenHeaders(token) }
  );
  check(res, { '좌석 예약 200': (r) => r.status === 200 });
  return res.status === 200 ? res.json('data.reservationId') : null;
}

// 결제 → paymentId 반환 (실패 시 null)
export function processPayment(token, userId, reservationId) {
  const res = http.post(
    `${BASE_URL}/api/v1/payments`,
    JSON.stringify({ userId, reservationId, paymentMethod: 'POINT' }),
    { headers: tokenHeaders(token) }
  );
  check(res, { '결제 200': (r) => r.status === 200 });
  return res.status === 200 ? res.json('data.paymentId') : null;
}

// 잔액 충전
export function chargePoint(userId, amount) {
  const res = http.patch(
    `${BASE_URL}/api/v1/users/${userId}/point`,
    JSON.stringify({ amount }),
    { headers: JSON_HEADERS }
  );
  check(res, { '잔액 충전 200': (r) => r.status === 200 });
  return res.status === 200;
}

// 예약 가능 좌석 조회 → seats 배열 반환
export function getAvailableSeats(concertDateId) {
  const res = http.get(`${BASE_URL}/api/v1/concerts/dates/${concertDateId}/seats`);
  check(res, { '좌석 조회 200': (r) => r.status === 200 });
  return res.status === 200 ? res.json('data') : [];
}
