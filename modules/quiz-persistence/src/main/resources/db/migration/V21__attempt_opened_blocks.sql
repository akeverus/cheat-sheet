-- Хендофф-3, Этап 9: телеметрия открытых блоков контента.
--
-- REST SubmitAttemptRequest несёт openedContentBlockIds — какие content-блоки
-- (код/схема/доп-материал) пользователь раскрывал перед ответом. Полезно для
-- будущей аналитики «смотрел ли подсказку». Хранится как компактный
-- comma-separated список id блоков (блоков на вопрос единицы — JSONB избыточен).
-- Additive-колонка, nullable: старые попытки и no-JS-путь оставляют её пустой.

ALTER TABLE attempt ADD COLUMN IF NOT EXISTS opened_content_block_ids TEXT;
