-- Хендофф-3, UI-014: закладки и заметки к вопросам.
--
-- Пользователь может сохранить вопрос в закладки и снабдить его текстовой
-- заметкой. Однопользовательское приложение → без user-ключа. Одна закладка на
-- вопрос (UNIQUE question_id): повторное сохранение обновляет заметку.
-- ON DELETE CASCADE: удаление вопроса убирает висячую закладку.
-- Время — epoch-секунды BIGINT (единая модель времени).

CREATE TABLE IF NOT EXISTS bookmark (
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT    NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    note        TEXT,
    created_at  BIGINT    NOT NULL,
    updated_at  BIGINT    NOT NULL,
    UNIQUE (question_id)
);

CREATE INDEX IF NOT EXISTS idx_bookmark_created_at ON bookmark (created_at);
