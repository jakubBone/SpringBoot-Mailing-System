CREATE TABLE users (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role        VARCHAR(20)     NOT NULL
);