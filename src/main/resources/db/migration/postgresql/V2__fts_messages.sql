ALTER TABLE messages
ADD COLUMN search_vector tsvector
GENERATED ALWAYS AS (to_tsvector('simple', content)) STORED

CREATE INDEX idx_messages_search_vector
    ON messages USING GIN (search_vector);