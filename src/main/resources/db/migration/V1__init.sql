CREATE TABLE users
(
    user_id    UUID         NOT NULL,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(20),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE user_points
(
    user_point_id UUID           NOT NULL,
    user_id       UUID           NOT NULL,
    balance       DECIMAL(15, 2) NOT NULL DEFAULT 0,
    version       BIGINT         NOT NULL DEFAULT 0,
    created_at    TIMESTAMP      NOT NULL,
    updated_at    TIMESTAMP      NOT NULL,
    CONSTRAINT pk_user_points PRIMARY KEY (user_point_id),
    CONSTRAINT fk_user_points_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE point_transactions
(
    point_transaction_id UUID           NOT NULL,
    user_id              UUID           NOT NULL,
    amount               DECIMAL(15, 2) NOT NULL,
    balance_after        DECIMAL(15, 2) NOT NULL,
    type                 VARCHAR(20)    NOT NULL,
    related_id           UUID,
    created_at           TIMESTAMP      NOT NULL,
    updated_at           TIMESTAMP      NOT NULL,
    CONSTRAINT pk_point_transactions PRIMARY KEY (point_transaction_id)
);

CREATE TABLE queue_tokens
(
    queue_token_id    UUID         NOT NULL,
    user_id           UUID         NOT NULL,
    token_value       VARCHAR(255) NOT NULL,
    position          INT          NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    estimated_wait_sec INT         NOT NULL DEFAULT 0,
    issued_at         TIMESTAMP    NOT NULL,
    activated_at      TIMESTAMP,
    expires_at        TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL,
    CONSTRAINT pk_queue_tokens PRIMARY KEY (queue_token_id),
    CONSTRAINT uq_queue_tokens_token_value UNIQUE (token_value)
);

CREATE TABLE concerts
(
    concert_id  UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    venue       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT pk_concerts PRIMARY KEY (concert_id)
);

CREATE TABLE concert_dates
(
    concert_date_id  UUID      NOT NULL,
    concert_id       UUID      NOT NULL,
    date             DATE      NOT NULL,
    total_seats      INT       NOT NULL,
    available_seats  INT       NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    CONSTRAINT pk_concert_dates PRIMARY KEY (concert_date_id),
    CONSTRAINT fk_concert_dates_concert FOREIGN KEY (concert_id) REFERENCES concerts (concert_id)
);

CREATE TABLE seats
(
    seat_id         UUID           NOT NULL,
    concert_date_id UUID           NOT NULL,
    seat_number     INT            NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    price           DECIMAL(15, 2) NOT NULL,
    version         BIGINT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP      NOT NULL,
    updated_at      TIMESTAMP      NOT NULL,
    CONSTRAINT pk_seats PRIMARY KEY (seat_id),
    CONSTRAINT fk_seats_concert_date FOREIGN KEY (concert_date_id) REFERENCES concert_dates (concert_date_id),
    CONSTRAINT uq_seats_concert_date_number UNIQUE (concert_date_id, seat_number)
);

CREATE TABLE reservations
(
    reservation_id  UUID         NOT NULL,
    user_id         UUID         NOT NULL,
    seat_id         UUID         NOT NULL,
    concert_date_id UUID         NOT NULL,
    token_value     VARCHAR(255) NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    reserved_at     TIMESTAMP    NOT NULL,
    confirmed_at    TIMESTAMP,
    expires_at      TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    CONSTRAINT pk_reservations PRIMARY KEY (reservation_id)
);

CREATE TABLE payments
(
    payment_id     UUID           NOT NULL,
    reservation_id UUID           NOT NULL,
    user_id        UUID           NOT NULL,
    amount         DECIMAL(15, 2) NOT NULL,
    method         VARCHAR(20)    NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    completed_at   TIMESTAMP,
    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP      NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (payment_id)
);
