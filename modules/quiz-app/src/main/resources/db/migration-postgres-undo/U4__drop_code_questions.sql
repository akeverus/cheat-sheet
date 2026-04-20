-- Rollback V4__code_questions.sql (PostgreSQL)
ALTER TABLE questions DROP COLUMN IF EXISTS question_type;
ALTER TABLE questions DROP COLUMN IF EXISTS code_snippet;
