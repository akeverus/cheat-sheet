-- Hybrid multi-user: scope mutable progress by owner_key.
-- Existing rows backfill to 'local' (matches app ownershipKey() in local mode).

-- ── review_state ──────────────────────────────────────────────────────────────
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE review_state DROP CONSTRAINT IF EXISTS review_state_pkey;
ALTER TABLE review_state ADD PRIMARY KEY (owner_key, question_id);
CREATE INDEX IF NOT EXISTS idx_review_state_owner_next
    ON review_state (owner_key, next_review_at);

-- ── bookmark ──────────────────────────────────────────────────────────────────
ALTER TABLE bookmark ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE bookmark DROP CONSTRAINT IF EXISTS bookmark_question_id_key;
ALTER TABLE bookmark DROP CONSTRAINT IF EXISTS uq_bookmark_question;
ALTER TABLE bookmark ADD CONSTRAINT uq_bookmark_owner_question UNIQUE (owner_key, question_id);
CREATE INDEX IF NOT EXISTS idx_bookmark_owner_created ON bookmark (owner_key, created_at);

-- ── paused_session (one pause per owner) ──────────────────────────────────────
ALTER TABLE paused_session ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE paused_session DROP CONSTRAINT IF EXISTS paused_session_id_check;
ALTER TABLE paused_session DROP CONSTRAINT IF EXISTS paused_session_pkey;
ALTER TABLE paused_session ADD PRIMARY KEY (owner_key);

-- ── attempt ───────────────────────────────────────────────────────────────────
ALTER TABLE attempt ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
DROP INDEX IF EXISTS uq_attempt_first_per_question;
CREATE UNIQUE INDEX uq_attempt_first_per_owner_question
    ON attempt (owner_key, question_id) WHERE is_first_attempt = true;
DROP INDEX IF EXISTS uq_attempt_idempotency_key;
CREATE UNIQUE INDEX uq_attempt_idempotency_key
    ON attempt (owner_key, idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_attempt_owner_question ON attempt (owner_key, question_id);
CREATE INDEX IF NOT EXISTS idx_attempt_owner_created ON attempt (owner_key, created_at);

-- ── attempt_idempotency_claim ─────────────────────────────────────────────────
ALTER TABLE attempt_idempotency_claim ADD COLUMN IF NOT EXISTS owner_key TEXT NOT NULL DEFAULT 'local';
ALTER TABLE attempt_idempotency_claim DROP CONSTRAINT IF EXISTS attempt_idempotency_claim_pkey;
ALTER TABLE attempt_idempotency_claim ADD PRIMARY KEY (owner_key, idempotency_key);
