#!/usr/bin/env python3
"""Инсертит варианты ответа из JSON в answer_options. quality_profile_version=2.

Usage:
    python3 scripts/insert-options.py path/to/batch.json
JSON format: см. контракт interview-options-writer.
"""
from __future__ import annotations
import json
import sqlite3
import sys
from pathlib import Path

DB = Path(__file__).resolve().parent.parent / "modules/quiz-app/data/db/interview.db"
SOURCE = "CLAUDE"
PROMPT_VERSION = 2
QUALITY_PROFILE_VERSION = 2


def main(json_path: str) -> int:
    data = json.loads(Path(json_path).read_text(encoding="utf-8"))
    if not isinstance(data, list):
        print("expected JSON array", file=sys.stderr)
        return 2

    conn = sqlite3.connect(str(DB))
    conn.execute("PRAGMA foreign_keys = ON")
    inserted_q = 0
    inserted_o = 0

    try:
        conn.execute("BEGIN")
        for entry in data:
            qid = entry["qid"]
            opts = entry["options"]
            if len(opts) != 4:
                raise ValueError(f"qid {qid}: expected 4 options, got {len(opts)}")
            correct_count = sum(1 for o in opts if o.get("correct"))
            if correct_count != 1:
                raise ValueError(f"qid {qid}: expected 1 correct, got {correct_count}")

            existing = conn.execute(
                "SELECT COUNT(*) FROM answer_options WHERE question_id = ?", (qid,)
            ).fetchone()[0]
            if existing:
                print(f"qid {qid}: skip (has {existing} options already)")
                continue

            for idx, o in enumerate(opts):
                conn.execute(
                    """
                    INSERT INTO answer_options
                      (question_id, option_text, is_correct, display_order,
                       source, explanation, prompt_version, quality_profile_version)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    (
                        qid,
                        o["text"],
                        1 if o["correct"] else 0,
                        idx,
                        SOURCE,
                        o.get("explanation"),
                        PROMPT_VERSION,
                        QUALITY_PROFILE_VERSION,
                    ),
                )
                inserted_o += 1
            inserted_q += 1
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"ROLLBACK: {e}", file=sys.stderr)
        return 1
    finally:
        conn.close()

    print(f"OK: questions={inserted_q} options={inserted_o}")
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: insert-options.py BATCH.json", file=sys.stderr)
        sys.exit(2)
    sys.exit(main(sys.argv[1]))
