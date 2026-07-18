-- Хендофф-3, Этап 9: геймификация опыта (XP) + рекорд серии.
--
-- Мотивация: экраны разбора и итогов показывают «+N XP», «Опыт», «Новый рекорд».
-- XP не выдумывается — это детерминированная агрегация реальных попыток
-- (см. docs/METRICS_GLOSSARY.md, ExperienceService.award). «Серия» берётся из
-- daily_activity (V10), а «Рекорд» (максимум серии за всю историю) хранится здесь.
--
-- Приложение однопользовательское (как daily_activity — без user-ключа), поэтому
-- таблица держит ровно одну строку (id = 1). Уровень (level) НЕ хранится — он
-- выводится из xp формулой в ExperienceService (единый источник — глоссарий).
-- Время — epoch-секунды BIGINT, как в остальном планировщике.

CREATE TABLE IF NOT EXISTS user_experience (
    id          INT    PRIMARY KEY DEFAULT 1,
    xp          BIGINT NOT NULL DEFAULT 0,
    best_streak INT    NOT NULL DEFAULT 0,
    updated_at  BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT user_experience_singleton CHECK (id = 1)
);

-- Гарантируем наличие строки-синглтона, чтобы ExperienceService работал через
-- UPDATE без предварительной проверки существования.
INSERT INTO user_experience (id, xp, best_streak, updated_at)
VALUES (1, 0, 0, 0)
ON CONFLICT (id) DO NOTHING;
