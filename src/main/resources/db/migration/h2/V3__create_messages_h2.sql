CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_sender
      FOREIGN KEY (sender_id)
      REFERENCES users(id)
      ON DELETE CASCADE,

    CONSTRAINT fk_recipient
      FOREIGN KEY (recipient_id)
      REFERENCES users(id)
      ON DELETE CASCADE
);