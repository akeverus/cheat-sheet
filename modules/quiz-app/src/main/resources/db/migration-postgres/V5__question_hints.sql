CREATE TABLE question_hints (
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT  NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    level       INT     NOT NULL,
    hint_text   TEXT    NOT NULL,
    created_at  BIGINT  NOT NULL,
    UNIQUE (question_id, level)
);
CREATE INDEX idx_hints_question ON question_hints(question_id);
