-- Ежедневная активность для системы streak и целей
CREATE TABLE IF NOT EXISTS daily_activity (
    activity_date TEXT PRIMARY KEY,       -- 'YYYY-MM-DD'
    questions_answered INTEGER DEFAULT 0,
    correct_count INTEGER DEFAULT 0,
    daily_goal INTEGER DEFAULT 10
);
