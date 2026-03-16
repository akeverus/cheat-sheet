-- Rollback V2__fulltext.sql (PostgreSQL)
DROP INDEX IF EXISTS idx_questions_search_vector;
DROP TRIGGER IF EXISTS questions_search_vector_update ON questions;
DROP FUNCTION IF EXISTS questions_search_vector_trigger();
ALTER TABLE questions DROP COLUMN IF EXISTS search_vector;
