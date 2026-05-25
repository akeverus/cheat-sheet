-- Схемный guard: выравниваем колонки, добавленные в прошлых миграциях
-- (без изменения checksum у уже выполненных V3/V4/V6).
ALTER TABLE questions ADD COLUMN IF NOT EXISTS source_slug TEXT;
UPDATE questions SET source_slug = slug WHERE source_slug IS NULL;

ALTER TABLE questions ADD COLUMN IF NOT EXISTS question_type TEXT NOT NULL DEFAULT 'TEXT';
ALTER TABLE questions ADD COLUMN IF NOT EXISTS code_snippet TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS diagram_mermaid TEXT;
ALTER TABLE questions ADD COLUMN IF NOT EXISTS regen_count INTEGER NOT NULL DEFAULT 0;

-- Убираем дублирующий индекс (в V1 уже есть idx_review_state_next_review_at).
DROP INDEX IF EXISTS idx_review_state_next_review;

-- Составные индексы под частые фильтры и сортировки.
CREATE INDEX IF NOT EXISTS idx_questions_topic_important
    ON questions(topic, is_important);

CREATE INDEX IF NOT EXISTS idx_review_state_next_review_question
    ON review_state(next_review_at, question_id);
