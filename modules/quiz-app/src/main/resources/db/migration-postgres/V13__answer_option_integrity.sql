CREATE UNIQUE INDEX IF NOT EXISTS uq_answer_options_single_correct_per_question
    ON answer_options(question_id)
    WHERE is_correct = 1;
