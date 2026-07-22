-- Hybrid multi-user: scope XP / streak / topic mastery by owner_key.

-- ── user_experience (was singleton id=1) ──────────────────────────────────────
ALTER TABLE user_experience ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE user_experience DROP CONSTRAINT IF EXISTS user_experience_singleton;
ALTER TABLE user_experience DROP CONSTRAINT IF EXISTS user_experience_pkey;
ALTER TABLE user_experience ADD PRIMARY KEY (owner_key);

-- ── daily_activity ────────────────────────────────────────────────────────────
ALTER TABLE daily_activity ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE daily_activity DROP CONSTRAINT IF EXISTS daily_activity_pkey;
ALTER TABLE daily_activity ADD PRIMARY KEY (owner_key, activity_date);

-- ── user_topic_stats ──────────────────────────────────────────────────────────
ALTER TABLE user_topic_stats ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE user_topic_stats DROP CONSTRAINT IF EXISTS user_topic_stats_topic_key;
ALTER TABLE user_topic_stats DROP CONSTRAINT IF EXISTS uq_user_topic_stats_topic;
ALTER TABLE user_topic_stats ADD CONSTRAINT uq_user_topic_stats_owner_topic UNIQUE (owner_key, topic);
CREATE INDEX IF NOT EXISTS idx_user_topic_stats_owner_topic ON user_topic_stats (owner_key, topic);
