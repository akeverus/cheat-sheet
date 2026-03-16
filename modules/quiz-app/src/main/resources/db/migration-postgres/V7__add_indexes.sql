-- Индексы для ускорения частых запросов

-- Фильтрация по теме (используется в каждом запросе квиза)
CREATE INDEX IF NOT EXISTS idx_questions_topic ON questions(topic);

-- Фильтрация по избранным (★)
CREATE INDEX IF NOT EXISTS idx_questions_important ON questions(is_important);

-- Orphan cleanup при импорте MD-файлов
CREATE INDEX IF NOT EXISTS idx_questions_file_path ON questions(file_path);

-- SRS-выборка: следующие вопросы по дате повторения
CREATE INDEX IF NOT EXISTS idx_review_state_next_review ON review_state(next_review_at);

-- Быстрый подсчёт вариантов ответов по вопросу
CREATE INDEX IF NOT EXISTS idx_answer_options_question_id ON answer_options(question_id);

-- Быстрый поиск подсказок по вопросу
CREATE INDEX IF NOT EXISTS idx_question_hints_question_id ON question_hints(question_id);
