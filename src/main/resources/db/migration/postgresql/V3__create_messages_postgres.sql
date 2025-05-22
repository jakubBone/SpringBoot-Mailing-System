CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id VARCHAR NOT NULL,
    recipient_id VARCHAR NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL

);