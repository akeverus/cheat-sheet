-- V3/V4/V6 уже создают нужные поля. Здесь только выравниваем данные и индексы,
-- не изменяя ранее выполненные миграции.
UPDATE questions SET source_slug = slug WHERE source_slug IS NULL;
UPDATE questions SET question_type = 'TEXT' WHERE question_type IS NULL;
UPDATE questions SET regen_count = 0 WHERE regen_count IS NULL;

-- Убираем дублирующий индекс (в V1 уже есть idx_review_state_next_review_at).
DROP INDEX IF EXISTS idx_review_state_next_review;

-- Составные индексы под частые фильтры и сортировки.
CREATE INDEX IF NOT EXISTS idx_questions_topic_important
    ON questions(topic, is_important);

CREATE INDEX IF NOT EXISTS idx_review_state_next_review_question
    ON review_state(next_review_at, question_id);
