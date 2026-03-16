ALTER TABLE questions ADD COLUMN question_type TEXT NOT NULL DEFAULT 'TEXT';

ALTER TABLE questions ADD COLUMN code_snippet TEXT;
