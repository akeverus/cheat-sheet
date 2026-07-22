-- BE-006: только одна строка с is_first_attempt=true на question_id.
-- При гонке двух concurrent first attempts второй INSERT получает unique violation
-- и перезаписывается с is_first_attempt=false в AttemptRepository.
CREATE UNIQUE INDEX IF NOT EXISTS uq_attempt_first_per_question
    ON attempt (question_id) WHERE is_first_attempt = true;
