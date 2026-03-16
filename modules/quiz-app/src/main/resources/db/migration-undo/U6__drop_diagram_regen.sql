-- Rollback V6__question_diagram_regen.sql
-- SQLite не поддерживает DROP COLUMN до 3.35.0
-- Для старых версий необходимо пересоздать таблицу вручную.
ALTER TABLE questions DROP COLUMN diagram_mermaid;
ALTER TABLE questions DROP COLUMN regen_count;
