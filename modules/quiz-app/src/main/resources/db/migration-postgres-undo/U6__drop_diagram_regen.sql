-- Rollback V6__question_diagram_regen.sql (PostgreSQL)
ALTER TABLE questions DROP COLUMN IF EXISTS diagram_mermaid;
ALTER TABLE questions DROP COLUMN IF EXISTS regen_count;
