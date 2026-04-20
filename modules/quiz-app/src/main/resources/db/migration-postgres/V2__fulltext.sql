-- Колонка для полнотекстового поиска
ALTER TABLE questions ADD COLUMN IF NOT EXISTS search_vector tsvector;

-- Функция обновления search_vector
CREATE OR REPLACE FUNCTION questions_search_vector_trigger() RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('russian', coalesce(NEW.question_text, '')), 'A') ||
        setweight(to_tsvector('russian', coalesce(NEW.answer_markdown, '')), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер на INSERT и UPDATE
DROP TRIGGER IF EXISTS questions_search_vector_update ON questions;
CREATE TRIGGER questions_search_vector_update
    BEFORE INSERT OR UPDATE OF question_text, answer_markdown
    ON questions
    FOR EACH ROW
    EXECUTE FUNCTION questions_search_vector_trigger();

-- Заполнить для существующих строк
UPDATE questions SET search_vector =
    setweight(to_tsvector('russian', coalesce(question_text, '')), 'A') ||
    setweight(to_tsvector('russian', coalesce(answer_markdown, '')), 'B')
WHERE search_vector IS NULL;

-- GIN-индекс для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_questions_search_vector ON questions USING gin(search_vector);
