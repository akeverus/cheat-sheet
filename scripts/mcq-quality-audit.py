#!/usr/bin/env python3
"""
MCQ Quality Audit для cheatsheets/interview/

Проверяет:
- 0 пробелов (каждый Q имеет ≥1 MCQ)
- Banned phrases (lazy filler от старого генератора)
- Placeholder MCQ (Правильный ответ описывает ...)
- Маркеры Tier 1 (📋 ПРАВИЛО, ❌ ПОСЛЕДСТВИЕ, ✓ ПРИМЕНЯТЬ, 🔗 См.)
- Длина-tells (correct >1.4× wrong-avg)
- Распределение [x] позиции (perfect ratio = 1.0)
- Dead cross-refs (🔗 См. Q<N> на несуществующий Q)

Usage:
  python3 scripts/mcq-quality-audit.py                # All files
  python3 scripts/mcq-quality-audit.py FILE.md        # Single file
  python3 scripts/mcq-quality-audit.py --top 10       # Top 10 worst
"""
from __future__ import annotations
import re
import sys
import glob
from pathlib import Path
from dataclasses import dataclass, field

BANNED_PHRASES = [
    "Частая ошибка в реальном коде",
    "Это антипаттерн или неправильный выбор в production",
    "Ключевое отличие и best practice in production",
    "Правильный ответ описывает основную концепцию",
    "Это смежное, но отличное понятие",
    "Объяснение концепции 2-3 предложения",
    "Неправильный вариант 1",
    "Неправильный вариант 2",
    "Неправильный вариант 3",
]

REQUIRED_WRONG_MARKERS = ["ПОСЛЕДСТВИЕ"]
REQUIRED_CORRECT_MARKERS = ["ПРАВИЛО", "ПРИМЕНЯТЬ", "См."]


@dataclass
class FileAudit:
    path: str
    total_q: int = 0
    total_mcq: int = 0
    zero_mcq_q: int = 0
    multi_mcq_q: int = 0
    banned_count: int = 0
    placeholder_count: int = 0
    wrong_no_consequence: int = 0
    correct_missing_marker: int = 0
    length_tells: int = 0
    broken_xrefs: int = 0
    pos_dist: list[int] = field(default_factory=lambda: [0, 0, 0, 0])

    @property
    def score(self) -> int:
        return (self.placeholder_count * 10
                + self.banned_count
                + self.wrong_no_consequence // 3
                + self.correct_missing_marker
                + self.length_tells * 2
                + self.broken_xrefs * 5)


def audit_file(fp: str) -> FileAudit:
    a = FileAudit(path=fp)
    text = Path(fp).read_text()
    file_q_nums = set(re.findall(r"^## Q(\d+)\.", text, re.M))
    a.total_q = len(file_q_nums)

    blocks = re.findall(r"(?:^|\n)(> \[!mcq\][^\n]*\n(?:>[^\n]*\n)+)", text)
    a.total_mcq = len(blocks)

    # Per-Q MCQ count
    parts = re.split(r"^(## Q\d+\.)", text, flags=re.M)
    for i in range(1, len(parts), 2):
        section = parts[i + 1] if i + 1 < len(parts) else ""
        c = section.count("> [!mcq]")
        if c == 0:
            a.zero_mcq_q += 1
        if c >= 2:
            a.multi_mcq_q += 1

    for block in blocks:
        lines = [l for l in block.split("\n") if re.match(r">\s*-\s*\[[ x]\]", l)]
        if len(lines) != 4:
            continue

        x_pos = next((i for i, l in enumerate(lines) if "[x]" in l), -1)
        if x_pos >= 0:
            a.pos_dist[x_pos] += 1

        correct = next((l for l in lines if "[x]" in l), "")
        wrongs = [l for l in lines if "[x]" not in l]

        # Banned + placeholder
        for phrase in BANNED_PHRASES:
            if phrase in block:
                a.banned_count += 1
                if "Правильный ответ описывает" in phrase:
                    a.placeholder_count += 1

        # Markers on wrong
        for w in wrongs:
            if not any(m in w for m in REQUIRED_WRONG_MARKERS):
                a.wrong_no_consequence += 1

        # Markers on correct
        if correct:
            missing = [m for m in REQUIRED_CORRECT_MARKERS if m not in correct]
            a.correct_missing_marker += len(missing)

            # Cross-refs
            for ref in re.findall(r"См\.\s*Q(\d+)", correct):
                if ref not in file_q_nums:
                    a.broken_xrefs += 1

        # Length disparity
        if correct and wrongs:
            cl = len(correct)
            wl_avg = sum(len(w) for w in wrongs) / len(wrongs)
            if cl > wl_avg * 1.4:
                a.length_tells += 1

    return a


def main():
    args = sys.argv[1:]
    top_n = None
    files = []

    if args and args[0] == "--top":
        top_n = int(args[1])
    elif args:
        files = args
    if not files:
        files = sorted(glob.glob("cheatsheets/interview/**/*.md", recursive=True))

    audits = [audit_file(fp) for fp in files]

    if top_n:
        audits = sorted(audits, key=lambda a: -a.score)[:top_n]

    print(f"{'FILE':<70} {'Q':>4} {'MCQ':>5} {'BAN':>4} {'PH':>3} {'WrM':>4} {'CrM':>4} {'LT':>3} {'BX':>3} {'POS':>15} SCORE")
    print("-" * 130)

    totals = FileAudit(path="TOTAL")
    for a in audits:
        rel = a.path.replace("cheatsheets/interview/", "")
        if len(rel) > 68:
            rel = "..." + rel[-65:]
        pos_str = "/".join(str(p) for p in a.pos_dist)
        print(f"{rel:<70} {a.total_q:>4} {a.total_mcq:>5} {a.banned_count:>4} {a.placeholder_count:>3} "
              f"{a.wrong_no_consequence:>4} {a.correct_missing_marker:>4} {a.length_tells:>3} "
              f"{a.broken_xrefs:>3} {pos_str:>15} {a.score}")

        totals.total_q += a.total_q
        totals.total_mcq += a.total_mcq
        totals.zero_mcq_q += a.zero_mcq_q
        totals.multi_mcq_q += a.multi_mcq_q
        totals.banned_count += a.banned_count
        totals.placeholder_count += a.placeholder_count
        totals.wrong_no_consequence += a.wrong_no_consequence
        totals.correct_missing_marker += a.correct_missing_marker
        totals.length_tells += a.length_tells
        totals.broken_xrefs += a.broken_xrefs
        for i in range(4):
            totals.pos_dist[i] += a.pos_dist[i]

    print("-" * 130)
    pos_str = "/".join(str(p) for p in totals.pos_dist)
    print(f"{'TOTAL':<70} {totals.total_q:>4} {totals.total_mcq:>5} {totals.banned_count:>4} "
          f"{totals.placeholder_count:>3} {totals.wrong_no_consequence:>4} {totals.correct_missing_marker:>4} "
          f"{totals.length_tells:>3} {totals.broken_xrefs:>3} {pos_str:>15}")
    print(f"\nPosition imbalance: {max(totals.pos_dist) / max(min(totals.pos_dist), 1):.1f}x "
          f"(ideal: 1.0x, sane: ≤2.0x)")
    print(f"Coverage: {(totals.total_q - totals.zero_mcq_q) * 100 // max(totals.total_q, 1)}% "
          f"({totals.total_q - totals.zero_mcq_q}/{totals.total_q})")
    print(f"Multi-MCQ: {totals.multi_mcq_q * 100 // max(totals.total_q, 1)}% "
          f"({totals.multi_mcq_q}/{totals.total_q})")

    return 1 if totals.banned_count or totals.placeholder_count else 0


if __name__ == "__main__":
    sys.exit(main())
