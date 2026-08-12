-- История завершённых сессий.
--
-- До этой таблицы сводка сессии жила ТОЛЬКО в HttpSession: экран итогов читал её
-- один раз и терял при следующем старте, перезагрузке или закрытии вкладки. То есть
-- вопрос «сколько я занимался на прошлой неделе и как шёл» в продукте не имел ответа
-- вовсе — ни в аналитике, ни где-либо ещё.
--
-- Хранятся ИТОГИ, а не ответы: подробности каждой попытки уже лежат в attempt и
-- review_state, и копировать их сюда значило бы завести второй источник правды о
-- том же событии. Здесь — то, что нельзя восстановить: границы самой сессии
-- (когда, в каком режиме, сколько заняла) и её счёт.
--
-- owner_key — как у остальных таблиц прогресса (V24): в local-режиме это 'local'.
CREATE TABLE IF NOT EXISTS session_history (
    id BIGSERIAL PRIMARY KEY,
    owner_key TEXT NOT NULL DEFAULT 'local',
    finished_at BIGINT NOT NULL,          -- epoch-секунды завершения
    mode TEXT NOT NULL,                   -- InterviewMode.name()
    total_questions INTEGER NOT NULL,
    correct_count INTEGER NOT NULL,
    wrong_count INTEGER NOT NULL,
    unknown_count INTEGER NOT NULL,
    duration_seconds BIGINT NOT NULL,
    session_xp BIGINT NOT NULL DEFAULT 0
);

-- Читается всегда одинаково: последние сессии владельца по времени вниз.
CREATE INDEX IF NOT EXISTS idx_session_history_owner_finished
    ON session_history (owner_key, finished_at DESC);
