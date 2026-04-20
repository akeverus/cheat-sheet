-- Rollback V3__question_expansion.sql (PostgreSQL)
ALTER TABLE questions DROP COLUMN IF EXISTS source_slug;
