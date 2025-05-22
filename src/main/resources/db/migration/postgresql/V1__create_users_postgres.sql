CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    provider VARCHAR(50) -- eg. 'GITHUB', 'LOCAL'
);


