#!/usr/bin/env python3
"""
Offline-сид вариантов ответа (`answer_options`) через Claude (Anthropic SDK).

Зачем
-----
В БД есть ~8.4k вопросов, но у большинства нет опций A/B/C/D. При первом показе
приложение ходит в OpenAI/DeepSeek, что даёт cold-start, тратит токены и зависит
от сетевого ключа. Этот скрипт один раз генерит опции оффлайн и пишет прямо
в `answer_options`, после чего рантайму AI не нужен.

Как работает
------------
1) Выбирает вопросы из SQLite, у которых 0 опций (LEFT JOIN answer_options).
2) Батчит по N вопросов и шлёт одним запросом Claude с **prompt caching**
   на системном промпте (90% экономии на повторных вызовах).
3) Ожидает строгий JSON-массив `[{qid, options: [{text, correct, explanation}, x4]}]`.
4) Валидирует контракт (4 опции, ровно 1 correct, непустые тексты) и вставляет
   в `answer_options` в одной транзакции на батч. source = 'CLAUDE'.
5) Идемпотентен: повторный запуск пропустит заполненные вопросы; `--overwrite`
   перезапишет.

Requirements
------------
    pip install anthropic

    export ANTHROPIC_API_KEY=sk-ant-...

Usage
-----
    # dry-run: посчитать что бы сделали
    python3 scripts/seed-options.py --dry-run

    # первые 100 без опций, батчами по 10, sonnet
    python3 scripts/seed-options.py --limit 100 --batch 10

    # фильтр по файлу (glob по file_path в БД)
    python3 scripts/seed-options.py --topic 'programming-languages/java/%'

    # полный прогон с другой моделью
    python3 scripts/seed-options.py --model claude-opus-4-7 --batch 20
"""
from __future__ import annotations

import argparse
import json
import logging
import os
import sqlite3
import sys
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Any

try:
    import anthropic  # type: ignore[import-not-found]
except ImportError:
    anthropic = None  # type: ignore[assignment]

PROJECT_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_DB = PROJECT_ROOT / "modules/quiz-app/data/db/interview.db"
DEFAULT_PROMPT = PROJECT_ROOT / "modules/quiz-app/src/main/resources/prompts/option.txt"

SOURCE_LABEL = "CLAUDE"
PROMPT_VERSION = 1
QUALITY_PROFILE_VERSION = 1

log = logging.getLogger("seed-options")


# --------------------------- data types ---------------------------


@dataclass(frozen=True)
class QuestionRow:
    qid: int
    topic: str
    question_text: str
    answer_markdown: str
    code_snippet: str | None


@dataclass
class OptionRow:
    text: str
    correct: bool
    explanation: str | None


@dataclass
class BatchStats:
    requested: int = 0
    inserted_questions: int = 0
    inserted_options: int = 0
    skipped: int = 0
    failed: int = 0
    input_tokens: int = 0
    cached_input_tokens: int = 0
    output_tokens: int = 0


# --------------------------- prompts ---------------------------


SYSTEM_PROMPT_HEADER = (
    "Ты эксперт-интервьюер по ИТ-темам: готовишь multiple-choice варианты ответов "
    "на технические вопросы уровня Middle–Senior. Русский язык. Техническая точность "
    "важнее стиля.\n\n"
    "ТЫ ПОЛУЧИШЬ БАТЧ вопросов с эталонным ответом в markdown. Для КАЖДОГО вопроса "
    "вернёшь РОВНО 4 варианта, из них РОВНО 1 правильный, + короткое объяснение.\n\n"
    "Правила качества опций (контракт):\n"
)


def load_option_contract(prompt_file: Path) -> str:
    """Читает контракт из prompts/option.txt и возвращает только пункты правил."""
    text = prompt_file.read_text(encoding="utf-8")
    # Берём блок от "Обязательные правила:" до "Финальный self-check"
    start_marker = "Обязательные правила:"
    end_marker = "Финальный self-check"
    s = text.find(start_marker)
    e = text.find(end_marker)
    if s == -1 or e == -1:
        # fallback: вернуть всё
        return text
    return text[s:e].strip()


RESPONSE_CONTRACT = """
Формат ответа — СТРОГО JSON без markdown, без комментариев, без ```, одним массивом:

[
  {
    "qid": <int>,
    "options": [
      {"text": "...", "correct": true,  "explanation": "..."},
      {"text": "...", "correct": false, "explanation": "..."},
      {"text": "...", "correct": false, "explanation": "..."},
      {"text": "...", "correct": false, "explanation": "..."}
    ]
  },
  ...
]

- `qid` совпадает с тем, что дан в вопросе.
- Порядок опций произвольный, `correct` в любой позиции.
- `explanation` 1-2 предложения: почему correct верен ИЛИ почему distractor неверен.
- Никакого текста вне JSON-массива.
""".strip()


def build_system_prompt(contract: str) -> str:
    return f"{SYSTEM_PROMPT_HEADER}{contract}\n\n{RESPONSE_CONTRACT}"


def build_user_message(batch: list[QuestionRow]) -> str:
    lines = ["Батч вопросов. Сгенерируй опции для каждого.\n"]
    for q in batch:
        lines.append(f"--- qid={q.qid} (topic={q.topic}) ---")
        lines.append(f"Вопрос: {q.question_text}")
        if q.code_snippet:
            lines.append(f"Код:\n```\n{q.code_snippet}\n```")
        # answer_markdown может быть большим — обрежем до 2500 символов
        ans = q.answer_markdown
        if len(ans) > 2500:
            ans = ans[:2500] + "\n...[truncated]"
        lines.append(f"Эталонный ответ:\n{ans}")
        lines.append("")
    lines.append("Верни JSON-массив по контракту. Никаких префиксов/суффиксов.")
    return "\n".join(lines)


# --------------------------- db ---------------------------


def fetch_pending_questions(
    conn: sqlite3.Connection,
    limit: int | None,
    topic_like: str | None,
    overwrite: bool,
) -> list[QuestionRow]:
    """Возвращает вопросы без опций (или все при overwrite)."""
    where_parts = []
    params: list[Any] = []
    if not overwrite:
        where_parts.append("ao.id IS NULL")
    if topic_like:
        where_parts.append("q.file_path LIKE ?")
        params.append(topic_like)
    where_sql = f"WHERE {' AND '.join(where_parts)}" if where_parts else ""

    sql = f"""
        SELECT q.id, q.topic, q.question_text, q.answer_markdown, q.code_snippet
        FROM questions q
        LEFT JOIN answer_options ao ON ao.question_id = q.id
        {where_sql}
        GROUP BY q.id
        ORDER BY q.id
    """
    if limit:
        sql += " LIMIT ?"
        params.append(limit)

    cur = conn.execute(sql, params)
    return [
        QuestionRow(
            qid=row[0],
            topic=row[1],
            question_text=row[2],
            answer_markdown=row[3],
            code_snippet=row[4],
        )
        for row in cur.fetchall()
    ]


def write_options(
    conn: sqlite3.Connection,
    qid: int,
    options: list[OptionRow],
    overwrite: bool,
) -> int:
    """Идемпотентная запись: при overwrite удаляет старые опции и вставляет новые."""
    if overwrite:
        conn.execute("DELETE FROM answer_options WHERE question_id = ?", (qid,))

    rows = [
        (
            qid,
            opt.text,
            1 if opt.correct else 0,
            idx,
            SOURCE_LABEL,
            opt.explanation,
            PROMPT_VERSION,
            QUALITY_PROFILE_VERSION,
        )
        for idx, opt in enumerate(options)
    ]
    conn.executemany(
        """
        INSERT INTO answer_options
            (question_id, option_text, is_correct, display_order,
             source, explanation, prompt_version, quality_profile_version)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """,
        rows,
    )
    return len(rows)


# --------------------------- claude ---------------------------


def call_claude(
    client: anthropic.Anthropic,
    model: str,
    system_prompt: str,
    user_message: str,
    max_tokens: int,
) -> tuple[str, dict[str, int]]:
    """Возвращает (text, usage_dict). Бросает исключение при сбое API."""
    resp = client.messages.create(
        model=model,
        max_tokens=max_tokens,
        system=[
            {
                "type": "text",
                "text": system_prompt,
                "cache_control": {"type": "ephemeral"},
            }
        ],
        messages=[{"role": "user", "content": user_message}],
    )
    text_blocks = [b.text for b in resp.content if getattr(b, "type", None) == "text"]
    text = "".join(text_blocks).strip()
    usage = {
        "input": getattr(resp.usage, "input_tokens", 0) or 0,
        "cache_read": getattr(resp.usage, "cache_read_input_tokens", 0) or 0,
        "cache_write": getattr(resp.usage, "cache_creation_input_tokens", 0) or 0,
        "output": getattr(resp.usage, "output_tokens", 0) or 0,
    }
    return text, usage


# --------------------------- parsing / validation ---------------------------


def strip_json_fences(text: str) -> str:
    t = text.strip()
    if t.startswith("```"):
        # drop first fence line and last fence
        lines = t.splitlines()
        if lines and lines[0].startswith("```"):
            lines = lines[1:]
        if lines and lines[-1].strip().startswith("```"):
            lines = lines[:-1]
        t = "\n".join(lines).strip()
    return t


def parse_and_validate(
    text: str, expected_qids: set[int]
) -> dict[int, list[OptionRow]]:
    t = strip_json_fences(text)
    data = json.loads(t)
    if not isinstance(data, list):
        raise ValueError("ожидался JSON-массив на верхнем уровне")

    result: dict[int, list[OptionRow]] = {}
    seen: set[int] = set()
    for entry in data:
        if not isinstance(entry, dict):
            raise ValueError(f"элемент не объект: {entry!r}")
        qid = entry.get("qid")
        if not isinstance(qid, int):
            raise ValueError(f"qid не int: {qid!r}")
        if qid not in expected_qids:
            raise ValueError(f"qid {qid} не из батча")
        if qid in seen:
            raise ValueError(f"qid {qid} повторяется в ответе")
        seen.add(qid)

        opts_raw = entry.get("options")
        if not isinstance(opts_raw, list) or len(opts_raw) != 4:
            raise ValueError(f"qid {qid}: ожидалось 4 options, получено {len(opts_raw) if isinstance(opts_raw, list) else 'not-list'}")

        opts: list[OptionRow] = []
        correct_count = 0
        for o in opts_raw:
            if not isinstance(o, dict):
                raise ValueError(f"qid {qid}: option не объект")
            txt = o.get("text")
            cor = o.get("correct")
            expl = o.get("explanation")
            if not isinstance(txt, str) or not txt.strip():
                raise ValueError(f"qid {qid}: пустой text")
            if not isinstance(cor, bool):
                raise ValueError(f"qid {qid}: correct не bool")
            if cor:
                correct_count += 1
            opts.append(OptionRow(text=txt.strip(), correct=cor, explanation=expl))
        if correct_count != 1:
            raise ValueError(f"qid {qid}: ожидался ровно 1 correct, получено {correct_count}")
        result[qid] = opts

    missing = expected_qids - seen
    if missing:
        raise ValueError(f"в ответе отсутствуют qids: {sorted(missing)}")
    return result


# --------------------------- main loop ---------------------------


def process_batch(
    client: anthropic.Anthropic,
    conn: sqlite3.Connection,
    batch: list[QuestionRow],
    system_prompt: str,
    model: str,
    max_tokens: int,
    overwrite: bool,
    stats: BatchStats,
    dry_run: bool,
) -> None:
    stats.requested += len(batch)
    if dry_run:
        log.info("[dry-run] batch size=%d qids=%s", len(batch), [q.qid for q in batch])
        return

    user_msg = build_user_message(batch)
    expected = {q.qid for q in batch}

    try:
        text, usage = call_claude(client, model, system_prompt, user_msg, max_tokens)
    except Exception as e:
        log.error("Claude API error on batch %s: %s", [q.qid for q in batch], e)
        stats.failed += len(batch)
        return

    stats.input_tokens += usage["input"]
    stats.cached_input_tokens += usage["cache_read"]
    stats.output_tokens += usage["output"]

    try:
        parsed = parse_and_validate(text, expected)
    except Exception as e:
        log.error(
            "Parse/validation error on batch %s: %s. First 300 chars of response: %r",
            [q.qid for q in batch],
            e,
            text[:300],
        )
        stats.failed += len(batch)
        return

    # write in one txn
    try:
        conn.execute("BEGIN")
        for qid, opts in parsed.items():
            inserted = write_options(conn, qid, opts, overwrite)
            stats.inserted_options += inserted
            stats.inserted_questions += 1
        conn.commit()
    except Exception as e:
        conn.rollback()
        log.error("DB error on batch %s: %s", [q.qid for q in batch], e)
        stats.failed += len(batch)
        return

    log.info(
        "batch OK: qids=%d, tokens(in=%d cached=%d out=%d)",
        len(batch),
        usage["input"],
        usage["cache_read"],
        usage["output"],
    )


def chunked(seq: list[QuestionRow], size: int) -> list[list[QuestionRow]]:
    return [seq[i : i + size] for i in range(0, len(seq), size)]


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="Seed answer_options via Claude.")
    parser.add_argument("--db", default=str(DEFAULT_DB))
    parser.add_argument("--prompt-file", default=str(DEFAULT_PROMPT))
    parser.add_argument("--model", default="claude-sonnet-4-6")
    parser.add_argument("--batch", type=int, default=10)
    parser.add_argument("--limit", type=int, default=None, help="max questions to process")
    parser.add_argument("--topic", default=None, help="SQL LIKE pattern on file_path")
    parser.add_argument("--overwrite", action="store_true", help="re-generate even if options exist")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--max-tokens", type=int, default=4096)
    parser.add_argument("--verbose", "-v", action="store_true")
    parser.add_argument("--sleep", type=float, default=0.0, help="pause between batches, seconds")
    args = parser.parse_args(argv)

    logging.basicConfig(
        level=logging.DEBUG if args.verbose else logging.INFO,
        format="%(asctime)s %(levelname)s %(message)s",
        datefmt="%H:%M:%S",
    )

    api_key = os.environ.get("ANTHROPIC_API_KEY")
    if not args.dry_run:
        if anthropic is None:
            log.error("missing 'anthropic' package. Install: pip install anthropic")
            return 2
        if not api_key:
            log.error("ANTHROPIC_API_KEY is not set")
            return 2

    db_path = Path(args.db)
    if not db_path.exists():
        log.error("DB not found: %s", db_path)
        return 2

    prompt_file = Path(args.prompt_file)
    if not prompt_file.exists():
        log.error("Prompt contract file not found: %s", prompt_file)
        return 2

    contract = load_option_contract(prompt_file)
    system_prompt = build_system_prompt(contract)
    log.info("System prompt built: %d chars (will be cached by Anthropic)", len(system_prompt))

    conn = sqlite3.connect(str(db_path))
    conn.execute("PRAGMA foreign_keys = ON")

    pending = fetch_pending_questions(conn, args.limit, args.topic, args.overwrite)
    log.info("Questions to process: %d (batch=%d, model=%s)", len(pending), args.batch, args.model)
    if not pending:
        log.info("Nothing to do.")
        return 0

    client = (
        None
        if args.dry_run
        else anthropic.Anthropic(api_key=api_key)
    )

    stats = BatchStats()
    batches = chunked(pending, args.batch)
    t0 = time.time()
    for i, batch in enumerate(batches, 1):
        log.info("--- batch %d/%d (size=%d) ---", i, len(batches), len(batch))
        process_batch(
            client, conn, batch, system_prompt, args.model,
            args.max_tokens, args.overwrite, stats, args.dry_run,
        )
        if args.sleep > 0 and i < len(batches):
            time.sleep(args.sleep)
    conn.close()

    dt = time.time() - t0
    log.info("DONE in %.1fs", dt)
    log.info(
        "Stats: requested=%d inserted_questions=%d inserted_options=%d failed=%d",
        stats.requested, stats.inserted_questions, stats.inserted_options, stats.failed,
    )
    if not args.dry_run:
        log.info(
            "Tokens: input=%d cache_read=%d output=%d (cache_read — бесплатные повторы)",
            stats.input_tokens, stats.cached_input_tokens, stats.output_tokens,
        )
    return 0 if stats.failed == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
