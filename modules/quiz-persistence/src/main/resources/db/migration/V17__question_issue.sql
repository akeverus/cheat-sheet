-- FLOW-REPORT (UX-21): «Сообщить о проблеме» с вопросом. Пользователь отмечает
-- некорректный/устаревший/неясный вопрос прямо в тренажёре; репорты копятся для
-- ручного разбора (single-user инструмент — модерации нет, статус меняется вручную).
--
-- question_id — FK на questions с ON DELETE CASCADE (репорт бессмыслен без вопроса).
-- category/status хранятся как TEXT (домен-enum QuestionIssueCategory/Status).
-- comment опционален. Индексы: по вопросу (сколько репортов у вопроса) и по
-- статусу (сколько открытых на разбор).
CREATE TABLE IF NOT EXISTS question_issue (
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT    NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    category    TEXT      NOT NULL,
    comment     TEXT,
    status      TEXT      NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_question_issue_question ON question_issue (question_id);
CREATE INDEX IF NOT EXISTS idx_question_issue_status ON question_issue (status);
