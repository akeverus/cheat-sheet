-- Rollback V4__code_questions.sql
-- SQLite не поддерживает DROP COLUMN до 3.35.0
ALTER TABLE questions DROP COLUMN question_type;
ALTER TABLE questions DROP COLUMN code_snippet;
