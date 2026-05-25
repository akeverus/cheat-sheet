CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    slug TEXT NOT NULL UNIQUE,
    file_path TEXT NOT NULL,
    topic TEXT NOT NULL,
    question_text TEXT NOT NULL,
    answer_markdown TEXT NOT NULL,
    is_important SMALLINT NOT NULL DEFAULT 0,
    source_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS answer_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct SMALLINT NOT NULL DEFAULT 0,
    display_order INTEGER NOT NULL,
    source TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_state (
    question_id BIGINT PRIMARY KEY,
    repetitions INTEGER NOT NULL DEFAULT 0,
    interval_days INTEGER NOT NULL DEFAULT 0,
    ease_factor DOUBLE PRECISION NOT NULL DEFAULT 2.5,
    next_review_at BIGINT NOT NULL,
    last_result TEXT NOT NULL DEFAULT 'NEW',
    correct_count INTEGER NOT NULL DEFAULT 0,
    wrong_count INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_review_state_next_review_at
    ON review_state(next_review_at);

CREATE INDEX IF NOT EXISTS idx_answer_options_question_id
    ON answer_options(question_id);

CREATE INDEX IF NOT EXISTS idx_questions_topic
    ON questions(topic);
