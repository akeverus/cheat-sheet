-- Wave A / BE-001: claim idempotency key BEFORE SM-2/XP/session side effects.
-- Winner of INSERT owns the submit; losers replay evaluate-only.
CREATE TABLE IF NOT EXISTS attempt_idempotency_claim (
    idempotency_key TEXT PRIMARY KEY,
    created_at      BIGINT NOT NULL
);
