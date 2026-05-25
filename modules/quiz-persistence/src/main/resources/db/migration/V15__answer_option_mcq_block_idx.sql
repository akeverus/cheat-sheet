-- V15: Multi-block MCQ support — добавляем mcq_block_idx, переделываем unique-constraint.

ALTER TABLE answer_options ADD COLUMN IF NOT EXISTS mcq_block_idx INTEGER NOT NULL DEFAULT 0;

DROP INDEX IF EXISTS uq_answer_options_single_correct_per_question;

CREATE UNIQUE INDEX IF NOT EXISTS uq_answer_options_single_correct_per_question_block
    ON answer_options(question_id, mcq_block_idx)
    WHERE is_correct = 1;

CREATE INDEX IF NOT EXISTS ix_answer_options_question_block
    ON answer_options(question_id, mcq_block_idx, display_order);
