-- ФАЗА 1 аудит-инкремента: построчный факт ответа (attempt), лог ревизий контента
-- (question_revision) и additive-колонки review_state под FSRS-планировщик.
--
-- Мотивация: сейчас прогресс хранится только агрегатами (review_state) + history
-- в HTTP-сессии. Для честной аналитики (first-attempt accuracy, response time,
-- retention) и для истории грейдов FSRS нужен построчный лог попыток.
--
-- Время везде — epoch-секунды BIGINT, как next_review_at в review_state (единая
-- модель времени планировщика). Длительности (response_time_ms) — миллисекунды INT.

-- ── question_revision ─────────────────────────────────────────────────────────
-- Append-only лог ревизий контента вопроса. Новая запись создаётся при импорте
-- (INSERT вопроса) и при смене source_hash (контент отредактирован). checksum
-- переиспользует SHA-256 из questions.source_hash. publication_status — задел на
-- draft/archived (пока всегда PUBLISHED). Уникальность (question_id, revision_number).
CREATE TABLE IF NOT EXISTS question_revision (
    id                 BIGSERIAL PRIMARY KEY,
    question_id        BIGINT    NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    revision_number    INT       NOT NULL,
    checksum           TEXT      NOT NULL,
    publication_status TEXT      NOT NULL DEFAULT 'PUBLISHED',
    created_at         BIGINT    NOT NULL,
    UNIQUE (question_id, revision_number)
);

CREATE INDEX IF NOT EXISTS idx_question_revision_question ON question_revision (question_id);

-- ── attempt ───────────────────────────────────────────────────────────────────
-- Построчный факт одного ответа. question_revision_id — на какой ревизии контента
-- дан ответ (nullable: заполняется с Фазы 2). selected_option_id — выбранный
-- вариант (ON DELETE SET NULL: пересборка вариантов не должна стирать историю
-- попыток). outcome — домен-enum AttemptOutcome (CORRECT/WRONG/UNKNOWN/SKIP).
-- is_first_attempt — первая ли попытка по этому вопросу (для first-attempt accuracy).
-- memory_grade — SM-2 grade 0–5 (для истории грейдов FSRS). idempotency_key —
-- дедуп двойного POST (UNIQUE), поверх позиционной защиты FLOW-02.
CREATE TABLE IF NOT EXISTS attempt (
    id                   BIGSERIAL PRIMARY KEY,
    question_id          BIGINT    NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    question_revision_id BIGINT    REFERENCES question_revision(id) ON DELETE SET NULL,
    selected_option_id   BIGINT    REFERENCES answer_options(id) ON DELETE SET NULL,
    outcome              TEXT      NOT NULL,
    is_first_attempt     BOOLEAN   NOT NULL,
    response_time_ms     INT,
    memory_grade         INT,
    session_token        TEXT,
    idempotency_key      TEXT,
    created_at           BIGINT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_attempt_question ON attempt (question_id);
CREATE INDEX IF NOT EXISTS idx_attempt_created_at ON attempt (created_at);
CREATE INDEX IF NOT EXISTS idx_attempt_outcome ON attempt (outcome);
CREATE UNIQUE INDEX IF NOT EXISTS uq_attempt_idempotency_key
    ON attempt (idempotency_key) WHERE idempotency_key IS NOT NULL;

-- ── review_state: additive-колонки под FSRS ────────────────────────────────────
-- SM-2-колонки (repetitions/interval_days/ease_factor) остаются как есть — FSRS
-- работает параллельно за флагом app.scheduling.algorithm, SM-2 — fallback.
-- stability/difficulty — параметры памяти FSRS; lapses — счётчик забываний;
-- algo_version — какой алгоритм последним писал строку; last_reviewed_at — epoch
-- последнего ответа (для explainable-причины «последняя ошибка N дней назад»).
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS stability        DOUBLE PRECISION;
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS difficulty       DOUBLE PRECISION;
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS lapses           INT NOT NULL DEFAULT 0;
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS algo_version     TEXT;
ALTER TABLE review_state ADD COLUMN IF NOT EXISTS last_reviewed_at BIGINT;
