ALTER TABLE questions ADD COLUMN IF NOT EXISTS difficulty TEXT NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE questions ADD COLUMN IF NOT EXISTS short_explanation TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS detailed_explanation TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS common_mistake TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS tags TEXT;

CREATE TABLE IF NOT EXISTS user_topic_stats (
    id BIGSERIAL PRIMARY KEY,
    topic TEXT NOT NULL UNIQUE,
    correct INTEGER NOT NULL DEFAULT 0,
    incorrect INTEGER NOT NULL DEFAULT 0,
    mastery DOUBLE PRECISION NOT NULL DEFAULT 0,
    last_seen TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_topic_stats_topic ON user_topic_stats(topic);
