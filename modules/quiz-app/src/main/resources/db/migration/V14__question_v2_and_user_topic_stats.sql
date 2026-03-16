ALTER TABLE questions ADD COLUMN difficulty TEXT NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE questions ADD COLUMN short_explanation TEXT;
ALTER TABLE questions ADD COLUMN detailed_explanation TEXT;
ALTER TABLE questions ADD COLUMN common_mistake TEXT;
ALTER TABLE questions ADD COLUMN tags TEXT;

CREATE TABLE IF NOT EXISTS user_topic_stats (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    topic TEXT NOT NULL UNIQUE,
    correct INTEGER NOT NULL DEFAULT 0,
    incorrect INTEGER NOT NULL DEFAULT 0,
    mastery REAL NOT NULL DEFAULT 0,
    last_seen TEXT
);

CREATE INDEX IF NOT EXISTS idx_user_topic_stats_topic ON user_topic_stats(topic);
