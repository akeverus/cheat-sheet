#!/usr/bin/env python3
"""
anti_tell_scan.py — можно ли определить правильный ответ по форме, не читая смысл (ROUND-10, gate 7).

Anti-tell в ROUND-10 обязателен, но вторичен относительно linkage: находки чинятся минимальной
смысловой правкой, а не переписыванием source-bound блока (см. §6 методологии).

`longest` считается подсказкой только при ЗНАЧИМОМ отрыве: разница в 1-2 слова человеку недоступна,
а требование выровнять её приводит к раздуванию вариантов ради метрики, что прямо запрещено.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter
from pathlib import Path

# Отрыв correct от второго по длине, начиная с которого длина становится реальной подсказкой.
LONGEST_ABS_MARGIN = 4
LONGEST_REL_MARGIN = 0.15

ABS_MARKER = re.compile(
    r"\b(всегда|никогда|только|единственн\w*|люб(?:ой|ое|ая|ых|ым)|"
    r"вес|весь|вся|всё|все|всю|всей|всех|всем|целиком|обязательн\w*|нельзя|невозможно)\b", re.I)
HEDGE = re.compile(r"\b(обычно|как правило|зачастую|может быть|возможно|скорее всего|иногда|в целом)\b", re.I)


def block_tells(options: list[dict]) -> list[str]:
    correct = next(o for o in options if o["correct"])
    cl = correct["label"]
    wc = {o["label"]: len(o["text"].split()) for o in options}
    tells = []

    others = sorted((v for k, v in wc.items() if k != cl), reverse=True)
    gap = wc[cl] - others[0]
    if gap >= LONGEST_ABS_MARGIN and gap / max(others[0], 1) >= LONGEST_REL_MARGIN:
        tells.append(f"longest(+{gap}сл)")

    def unique_to_correct(pred) -> bool:
        hits = {o["label"]: pred(o["text"]) for o in options}
        return hits[cl] and not any(v for k, v in hits.items() if k != cl)

    def missing_only_in_correct(pred) -> bool:
        hits = {o["label"]: pred(o["text"]) for o in options}
        return not hits[cl] and all(v for k, v in hits.items() if k != cl)

    checks = {
        "backtick": lambda t: "`" in t,
        "digit": lambda t: bool(re.search(r"\d", t)),
        "colon": lambda t: ":" in t,
        "semicolon": lambda t: ";" in t,
        "abs-marker": lambda t: bool(ABS_MARKER.search(t)),
        "hedge": lambda t: bool(HEDGE.search(t)),
    }
    for name, pred in checks.items():
        if unique_to_correct(pred):
            tells.append(f"uniq-{name}")
        if missing_only_in_correct(pred):
            tells.append(f"gap-{name}")
    return tells


def scan(path: Path) -> dict:
    seed = json.loads(path.read_text(encoding="utf-8"))
    rows, dist = [], Counter()
    for q in seed["questions"]:
        for b in q["blocks"]:
            opts = b["options"]
            correct = next(o for o in opts if o["correct"])
            dist[correct["label"]] += 1
            wc = [len(o["text"].split()) for o in opts]
            rows.append({
                "tag": f"q{q['q_number']}b{b['block_idx']}",
                "correct": correct["label"],
                "words": {o["label"]: len(o["text"].split()) for o in opts},
                "word_parity": round(min(wc) / max(wc), 3),
                "tells": block_tells(opts),
            })
    return {"seed": str(path), "blocks": rows, "correct_distribution": dict(dist)}


def main() -> int:
    ap = argparse.ArgumentParser(description="Проверка формальных подсказок правильного ответа")
    ap.add_argument("files", nargs="+")
    args = ap.parse_args()
    total, flagged = 0, 0
    for f in args.files:
        r = scan(Path(f))
        print(f"\n=== {r['seed']}")
        for row in r["blocks"]:
            total += 1
            if row["tells"]:
                flagged += 1
                print(f"  TELL {row['tag']} correct={row['correct']} "
                      f"wpar={row['word_parity']} → {', '.join(row['tells'])}")
        print(f"  распределение correct: {r['correct_distribution']}")
    print(f"\n--- ИТОГ: блоков={total} с подсказкой={flagged}")
    return 1 if flagged else 0


if __name__ == "__main__":
    sys.exit(main())
