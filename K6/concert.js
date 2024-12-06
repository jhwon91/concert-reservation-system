import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 }, // 10초 동안 10명의 사용자
    { duration: '20s', target: 50 }, // 20초 동안 50명 유지
    { duration: '10s', target: 0 },  // 10초 동안 종료
  ],
};

export default function () {
  const BASE_URL = 'http://host.docker.internal:8080';
  const concertId = 1; // 테스트용 concert_id
  const concertDetailId = Math.floor(Math.random() * 100) + 1;; // 테스트용 concert_detail_id
  const userId = Math.floor(Math.random() * 10) + 1; // 랜덤 user_id

  // 1. 토큰 발급 요청
  const tokenPayload = JSON.stringify({ user_id:userId });
  const tokenHeaders = { 'Content-Type': 'application/json' };
  const tokenRes = http.post(`${BASE_URL}/api/tokens`, tokenPayload, { headers: tokenHeaders });

  console.log('Token Response:', tokenRes.body);

  check(tokenRes, {
    'Token request succeeded': (res) => res.status === 200,
    'Token has valid structure': (res) => res.json('token') !== undefined,
  });

  const token = tokenRes.json('token');
  if (!token) {
    console.error('Token generation failed!');
    return;
  }

  // 2. 예약 가능한 좌석 조회 요청
  const seatHeaders = { Authorization: token };

  const seatParams = `?concert_id=1&concert_detail_id=${concertDetailId}`;
  const seatRes = http.get(`${BASE_URL}/api/reservations/seats${seatParams}`, { headers: seatHeaders });

  console.log('Seat Response:', seatRes.body);

  check(seatRes, {
    'Seat request succeeded': (res) => res.status === 200,
    'Seat response is valid': (res) => res.json('availableSeats') !== undefined,
  });

  sleep(1);
}
