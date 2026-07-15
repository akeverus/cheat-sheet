-- FLOW-01: приостановленная сессия тренировки для resume между перезапусками
-- приложения. Приложение single-user (см. daily_activity/user_topic_stats —
-- ключей пользователя нет) → не более одной паузы одновременно: singleton-строка
-- с id = 1 (CHECK гарантирует инвариант).
--
-- session_blob — Java-сериализованный InterviewSession. Это тот же механизм,
-- которым объект уже персистится в HTTP-сессию (класс Serializable,
-- serialVersionUID=1), поэтому доменная модель не меняется. Денормализованные
-- колонки (mode/topic/total/answered/paused_at) нужны для баннера
-- «есть незавершённая сессия — продолжить?» без десериализации блоба.
CREATE TABLE IF NOT EXISTS paused_session (
    id           INTEGER   PRIMARY KEY DEFAULT 1 CHECK (id = 1),
    session_blob BYTEA     NOT NULL,
    mode         TEXT      NOT NULL,
    topic        TEXT,
    total        INTEGER   NOT NULL,
    answered     INTEGER   NOT NULL,
    paused_at    TIMESTAMP NOT NULL
);
