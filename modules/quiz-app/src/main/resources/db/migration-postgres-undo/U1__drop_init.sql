-- Rollback V1__init.sql (PostgreSQL)
-- ВНИМАНИЕ: удаляет ВСЕ данные! Используйте только для чистого отката.
DROP INDEX IF EXISTS idx_questions_topic;
DROP INDEX IF EXISTS idx_answer_options_question_id;
DROP INDEX IF EXISTS idx_review_state_next_review_at;
DROP TABLE IF EXISTS review_state;
DROP TABLE IF EXISTS answer_options;
DROP TABLE IF EXISTS questions;
