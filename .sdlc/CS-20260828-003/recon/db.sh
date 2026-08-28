#!/usr/bin/env bash
# CS-20260828-003 Блок B — живое состояние БД (§18.2).
# Запуск: bash .sdlc/CS-20260828-003/recon/db.sh
set -euo pipefail
OUT="$(dirname "$0")/../data/db-counts.txt"
PSQL="docker exec quiz-postgres psql -U interview -tA"

{
  echo "== recon-БД interview_recon =="
  echo "-- Q1: всего вопросов --"
  echo "SELECT count(*) FROM questions;"
  $PSQL -d interview_recon -c "SELECT count(*) FROM questions;"
  echo
  echo "-- Q2: вопросов без единого варианта (optionless) --"
  echo "SELECT count(*) FROM questions q WHERE NOT EXISTS (SELECT 1 FROM answer_options ao WHERE ao.question_id = q.id);"
  $PSQL -d interview_recon -c "SELECT count(*) FROM questions q WHERE NOT EXISTS (SELECT 1 FROM answer_options ao WHERE ao.question_id = q.id);"
  echo
  echo "-- Q3: распределение вопросов по числу вариантов (0/2/3/4/5+) --"
  echo "SELECT CASE WHEN cnt=0 THEN '0' WHEN cnt=2 THEN '2' WHEN cnt=3 THEN '3' WHEN cnt=4 THEN '4' ELSE '5+' END AS bucket, count(*) FROM (SELECT q.id, count(ao.id) AS cnt FROM questions q LEFT JOIN answer_options ao ON ao.question_id = q.id GROUP BY q.id) t GROUP BY bucket ORDER BY bucket;"
  $PSQL -d interview_recon -c "SELECT CASE WHEN cnt=0 THEN '0' WHEN cnt=2 THEN '2' WHEN cnt=3 THEN '3' WHEN cnt=4 THEN '4' ELSE '5+' END AS bucket, count(*) FROM (SELECT q.id, count(ao.id) AS cnt FROM questions q LEFT JOIN answer_options ao ON ao.question_id = q.id GROUP BY q.id) t GROUP BY bucket ORDER BY bucket;"
  echo
  echo "-- Q4: распределение правильного варианта по позициям (display_order, is_correct=1) --"
  echo "SELECT display_order, count(*) FROM answer_options WHERE is_correct = 1 GROUP BY display_order ORDER BY display_order;"
  $PSQL -d interview_recon -c "SELECT display_order, count(*) FROM answer_options WHERE is_correct = 1 GROUP BY display_order ORDER BY display_order;"
  echo
  echo "-- Q5: число тем --"
  echo "SELECT count(DISTINCT topic) FROM questions;"
  $PSQL -d interview_recon -c "SELECT count(DISTINCT topic) FROM questions;"
  echo
  echo "-- Q6: число категорий (первый сегмент topic до '/') --"
  echo "SELECT count(DISTINCT split_part(topic, '/', 1)) FROM questions;"
  $PSQL -d interview_recon -c "SELECT count(DISTINCT split_part(topic, '/', 1)) FROM questions;"
  echo

  echo "== пользовательская БД interview (read-only сверка) =="
  echo "-- Q1u: всего вопросов --"
  $PSQL -d interview -c "SELECT count(*) FROM questions;"
  echo
  echo "-- Q2u: optionless --"
  $PSQL -d interview -c "SELECT count(*) FROM questions q WHERE NOT EXISTS (SELECT 1 FROM answer_options ao WHERE ao.question_id = q.id);"
  echo
  echo "-- Q3u: distribution --"
  $PSQL -d interview -c "SELECT CASE WHEN cnt=0 THEN '0' WHEN cnt=2 THEN '2' WHEN cnt=3 THEN '3' WHEN cnt=4 THEN '4' ELSE '5+' END AS bucket, count(*) FROM (SELECT q.id, count(ao.id) AS cnt FROM questions q LEFT JOIN answer_options ao ON ao.question_id = q.id GROUP BY q.id) t GROUP BY bucket ORDER BY bucket;"
} > "$OUT"

cat "$OUT"
