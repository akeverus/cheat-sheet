-- Rollback V7__add_indexes.sql (PostgreSQL)
DROP INDEX IF EXISTS idx_questions_topic;
DROP INDEX IF EXISTS idx_questions_important;
DROP INDEX IF EXISTS idx_questions_file_path;
DROP INDEX IF EXISTS idx_review_state_next_review;
DROP INDEX IF EXISTS idx_answer_options_question_id;
DROP INDEX IF EXISTS idx_question_hints_question_id;
