-- Контроль качества контента в БД (PostgreSQL).
-- Использование:
--   docker compose exec -T postgres psql -U interview -d interview -f /tmp/quality-audit.sql
-- или скопировать блоки в psql по частям.

-- 1) Общий охват вопросов/опций/диаграмм.
SELECT
  COUNT(*) AS questions_total,
  COUNT(*) FILTER (WHERE question_text IS NULL OR btrim(question_text) = '') AS empty_question_text,
  COUNT(*) FILTER (WHERE question_text !~ '\\?$') AS question_without_qmark,
  COUNT(*) FILTER (WHERE diagram_mermaid IS NOT NULL AND btrim(diagram_mermaid) <> '') AS questions_with_diagram
FROM questions;

-- 2) Целостность опций на уровне вопроса.
WITH per_question AS (
  SELECT
    q.id,
    COUNT(ao.id) AS options_count,
    COUNT(*) FILTER (WHERE ao.is_correct = 1) AS correct_count,
    COUNT(*) FILTER (WHERE ao.option_text IS NULL OR btrim(ao.option_text) = '') AS empty_option_count,
    COUNT(*) FILTER (
      WHERE ao.option_text ~ '(/|\\.md|-interview|src/|modules/|cheatsheets/)'
    ) AS path_like_count,
    COUNT(*) FILTER (WHERE ao.explanation IS NULL OR btrim(ao.explanation) = '') AS empty_explanation_count
  FROM questions q
  LEFT JOIN answer_options ao ON ao.question_id = q.id
  GROUP BY q.id
)
SELECT
  COUNT(*) AS questions_total,
  COUNT(*) FILTER (WHERE options_count > 0) AS questions_with_options,
  ROUND(100.0 * COUNT(*) FILTER (WHERE options_count > 0) / NULLIF(COUNT(*), 0), 2) AS options_coverage_pct,
  COUNT(*) FILTER (WHERE options_count = 1) AS single_option_questions,
  COUNT(*) FILTER (WHERE options_count >= 2) AS multi_option_questions,
  COUNT(*) FILTER (WHERE correct_count <> 1 AND options_count > 0) AS invalid_correct_count_questions,
  COUNT(*) FILTER (WHERE empty_option_count > 0) AS has_empty_options_questions,
  COUNT(*) FILTER (WHERE path_like_count > 0) AS has_path_like_options_questions,
  COUNT(*) FILTER (WHERE empty_explanation_count > 0 AND options_count > 0) AS has_empty_explanations_questions
FROM per_question;

-- 2.1) Распределение числа вариантов на вопрос.
SELECT
  options_count,
  COUNT(*) AS questions_count
FROM (
  SELECT q.id, COUNT(ao.id) AS options_count
  FROM questions q
  LEFT JOIN answer_options ao ON ao.question_id = q.id
  GROUP BY q.id
) t
GROUP BY options_count
ORDER BY options_count;

-- 3) Источники опций и доля fallback.
SELECT
  source,
  COUNT(*) AS options_count,
  ROUND(100.0 * COUNT(*) / NULLIF((SELECT COUNT(*) FROM answer_options), 0), 2) AS options_share_pct,
  COUNT(*) FILTER (WHERE explanation IS NULL OR btrim(explanation) = '') AS empty_explanations
FROM answer_options
GROUP BY source
ORDER BY options_count DESC;

-- 4) Доля клишированных fallback-паттернов (быстрый smoke-check).
SELECT
  COUNT(*) AS total_wrong_options,
  COUNT(*) FILTER (WHERE is_correct = 0 AND option_text ~* '^описывают\\s+.+\\s+как\\s+общую\\s+идею\\b') AS cliche_describe,
  COUNT(*) FILTER (WHERE is_correct = 0 AND option_text ~* '^дают\\s+смежное\\s+определение\\b') AS cliche_adjacent_definition,
  COUNT(*) FILTER (WHERE is_correct = 0 AND option_text ~* '^подменяют\\s+смысл\\b') AS cliche_substitute,
  COUNT(*) FILTER (WHERE is_correct = 0 AND option_text ~* '^для\\s+.+\\s+(используют|применяют|выполняют)\\b') AS cliche_for_entity
FROM answer_options;

-- 5) Качество диаграмм (структура и базовый smoke-check).
WITH diagrams AS (
  SELECT
    id,
    slug,
    diagram_mermaid,
    array_length(regexp_split_to_array(diagram_mermaid, E'\\n'), 1) AS line_count
  FROM questions
  WHERE diagram_mermaid IS NOT NULL AND btrim(diagram_mermaid) <> ''
)
SELECT
  COUNT(*) AS diagrams_total,
  COUNT(*) FILTER (WHERE diagram_mermaid ~ '(^|\\n)```') AS has_markdown_fences,
  COUNT(*) FILTER (
    WHERE diagram_mermaid !~ '^(flowchart|graph|sequenceDiagram|classDiagram|stateDiagram|stateDiagram-v2|erDiagram|journey|gantt|mindmap|timeline)'
  ) AS invalid_start_token,
  COUNT(*) FILTER (WHERE line_count < 3) AS too_short,
  COUNT(*) FILTER (WHERE line_count > 30) AS too_long
FROM diagrams;
