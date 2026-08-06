-- Убираем строки прогресса, заведённые показом вопроса, а не ответом на него.
--
-- Раньше строку в review_state создавал сам показ карточки (ReviewStateRepository
-- .insertIfAbsent из InterviewService). Срок повторения ставился «сейчас», счётчики
-- оставались нулевыми — просмотренный, но ни разу не отвеченный вопрос становился для
-- базы неотличим от честно выученного и просроченного.
--
-- Видно это на /stats: QuestionStatsRepository.getScheduleAggregate считает overdue как
-- SUM(next_review_at <= cutoff) БЕЗ COALESCE, то есть только по существующим строкам.
-- Новый вопрос без строки в overdue не попадает — а такой фантом попадал, и счётчик
-- «просрочено» рос от одного пролистывания.
--
-- Показ больше ничего не пишет: строку создаёт первый ответ (update/updateFsrs стали
-- upsert). Но у уже работающих установок фантомы остались в базе и завышали бы overdue
-- бесконечно — их и удаляет эта миграция.
--
-- Удаление ничего не теряет: условие ниже — ровно та подпись, которую писал
-- insertIfAbsent (owner_key, question_id, next_review_at; остальное — умолчания схемы).
-- Информации в такой строке нет, и её отсутствие означает то же самое: «на вопрос ещё не
-- отвечали». Сброс после правки контента (ReviewStateRepository.reset) пишет
-- last_result = 'RESET', а не 'NEW', и под условие не попадает.
DELETE FROM review_state
WHERE last_result = 'NEW'
  AND repetitions = 0
  AND interval_days = 0
  AND correct_count = 0
  AND wrong_count = 0
  AND stability IS NULL
  AND difficulty IS NULL
  AND lapses = 0
  AND algo_version IS NULL;
