-- V15: Multi-block MCQ support — добавляем mcq_block_idx, переделываем unique-constraint.
-- Раньше: один MCQ-блок на question_id (только первый из markdown учитывался парсером).
-- Теперь: несколько MCQ-блоков на question_id, идентифицируются по индексу 0..N.

ALTER TABLE answer_options ADD COLUMN mcq_block_idx INTEGER NOT NULL DEFAULT 0;

-- SQLite не умеет ALTER INDEX или DROP-then-CREATE для partial unique index атомарно
-- без пересборки таблицы. Поэтому drop + create:
DROP INDEX IF EXISTS uq_answer_options_single_correct_per_question;

-- Новый constraint: один correct на (question_id, mcq_block_idx).
CREATE UNIQUE INDEX IF NOT EXISTS uq_answer_options_single_correct_per_question_block
    ON answer_options(question_id, mcq_block_idx)
    WHERE is_correct = 1;

-- Дополнительный индекс для быстрого получения всех опций конкретного блока:
CREATE INDEX IF NOT EXISTS ix_answer_options_question_block
    ON answer_options(question_id, mcq_block_idx, display_order);
