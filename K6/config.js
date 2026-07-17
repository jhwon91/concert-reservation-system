// k6 실행 방법:
//   로컬: k6 run k6/scenarios/queue-load.js
//   Docker: k6 run --env BASE_URL=http://host.docker.internal:8080 k6/scenarios/queue-load.js
//   환경변수 오버라이드: k6 run --env VUS=1000 k6/scenarios/queue-load.js

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 시드 데이터 UUID (V2__seed.sql과 맞춰야 함)
export const SEED = {
  userIds: [
    __ENV.USER_ID_1 || '00000000-0000-0000-0000-000000000001',
    __ENV.USER_ID_2 || '00000000-0000-0000-0000-000000000002',
    __ENV.USER_ID_3 || '00000000-0000-0000-0000-000000000003',
    __ENV.USER_ID_4 || '00000000-0000-0000-0000-000000000004',
    __ENV.USER_ID_5 || '00000000-0000-0000-0000-000000000005',
  ],
  concertId:     __ENV.CONCERT_ID      || '10000000-0000-0000-0000-000000000001',
  concertDateId: __ENV.CONCERT_DATE_ID || '20000000-0000-0000-0000-000000000001',
  // 동시성 테스트용 단일 좌석
  hotSeatId:     __ENV.HOT_SEAT_ID     || '30000000-0000-0000-0000-000000000001',
};

// 시나리오별 VU 수 (환경변수로 오버라이드 가능)
export const VUS = {
  queueLoad:       parseInt(__ENV.VUS)       || 500,
  pollingStorm:    parseInt(__ENV.VUS)       || 1000,
  seatConcurrency: parseInt(__ENV.VUS)       || 100,
};

// 공통 임계값
export const THRESHOLDS = {
  http_req_failed:   [{ threshold: 'rate<0.01', abortOnFail: false }],  // 에러율 1% 미만
  http_req_duration: [{ threshold: 'p(95)<2000', abortOnFail: false }], // p95 2초 미만
};
