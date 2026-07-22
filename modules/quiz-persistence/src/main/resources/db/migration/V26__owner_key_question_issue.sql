-- Hybrid multi-user: scope question issue reports by owner_key.
ALTER TABLE question_issue ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
CREATE INDEX IF NOT EXISTS idx_question_issue_owner_question
    ON question_issue (owner_key, question_id);
CREATE INDEX IF NOT EXISTS idx_question_issue_owner_status
    ON question_issue (owner_key, status);
