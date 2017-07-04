CREATE TABLE topics (
    id SERIAL PRIMARY KEY,
    topic TEXT,
    alias TEXT,
    content TEXT,
    email TEXT,
    secret BIGINT,
    date TIMESTAMP WITHOUT TIME ZONE
);
CREATE TABLE replies (
    id SERIAL PRIMARY KEY,
    topic_id BIGINT,
    alias TEXT,
    content TEXT,
    email TEXT,
    secret BIGINT,
    date TIMESTAMP WITHOUT TIME ZONE
);
