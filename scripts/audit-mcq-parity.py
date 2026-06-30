#!/usr/bin/env python3
"""Аудит «угадываемости» правильного варианта в MCQ JSON-сидерах.

Находит блоки, где correct можно угадать по форме (длине / структуре / насыщенности),
не зная темы. Скрипт НЕ принимает решение за человека — он только показывает кандидатов
на ручную правку. Правило-первоисточник: «Option Parity / Structural Parity» в скилле
~/.claude/skills/mcq-quality-fixer/SKILL.md.

Сигналы на уровне блока:
  LEN_AVG      len(correct) / avg(len(wrong)) > 1.3
  LEN_SPREAD   max(len опций) / min(len опций) > 1.5
  UNIQ_MARKER  correct содержит маркер структуры (→ | «: a; b» | ';' | ≥2 `backtick` |
               нумерованный/шаговый список), которого нет НИ В ОДНОМ wrong-варианте
  COMMA_GAP    запятых в correct заметно больше, чем в любом wrong (>1.5× и +3)
  SHORT_DISTR  есть wrong короче 60% длины correct (вариант-заглушка)

Сигнал на уровне файла:
  CORRECT_LONGEST_RATE  доля блоков, где correct — самый длинный вариант.
                        >0.5 при многих блоках = «самый длинный = правильный», файл угадывается.

Использование:
  python3 scripts/audit-mcq-parity.py [path-or-dir ...]   # по умолчанию весь seed/mcq
  python3 scripts/audit-mcq-parity.py --top 25            # худшие N файлов по числу флагов
  python3 scripts/audit-mcq-parity.py <file.json> -v      # подробно по каждому блоку
"""
import json
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
SEED = REPO / "modules/quiz-app/src/main/resources/seed/mcq"

LEN_AVG_THR = 1.3
LEN_SPREAD_THR = 1.5
SHORT_DISTR_THR = 0.6
COMMA_RATIO_THR = 1.5
COMMA_ABS_THR = 3

ARROW = re.compile(r"→|->|⇒")
ENUM_COLON = re.compile(r":\s*[^:]+[;,].+")          # «X: a; b» / «X: a, b, c»
SEMI = re.compile(r";")
NUM_LIST = re.compile(r"(^|\s)(?:1[\).]\s|шаг\s*1|этап\s*1)", re.IGNORECASE)


def backtick_count(s):
    return s.count("`") // 2


def markers(text):
    """Набор структурных маркеров, присутствующих в строке."""
    m = set()
    if ARROW.search(text):
        m.add("arrow")
    if ENUM_COLON.search(text):
        m.add("colon-enum")
    if SEMI.search(text):
        m.add("semicolon")
    if backtick_count(text) >= 2:
        m.add("multi-backtick")
    if NUM_LIST.search(text):
        m.add("step-list")
    return m


def audit_block(opts):
    """Вернуть список причин (str) для одного MCQ-блока."""
    reasons = []
    correct = next((o for o in opts if o.get("correct")), None)
    wrong = [o for o in opts if not o.get("correct")]
    if not correct or not wrong:
        return reasons
    cl = len(correct["text"])
    wl = [len(o["text"]) for o in wrong]
    avg_w = sum(wl) / len(wl)
    all_len = [len(o["text"]) for o in opts]

    if avg_w and cl / avg_w > LEN_AVG_THR:
        reasons.append(f"LEN_AVG {cl/avg_w:.2f}")
    if min(all_len) and max(all_len) / min(all_len) > LEN_SPREAD_THR:
        reasons.append(f"LEN_SPREAD {max(all_len)/min(all_len):.2f}")

    cm = markers(correct["text"])
    wm = set().union(*(markers(o["text"]) for o in wrong)) if wrong else set()
    uniq = cm - wm
    if uniq:
        reasons.append("UNIQ_MARKER " + "/".join(sorted(uniq)))

    cc = correct["text"].count(",")
    mwc = max((o["text"].count(",") for o in wrong), default=0)
    if cc >= mwc + COMMA_ABS_THR and (mwc == 0 or cc / mwc > COMMA_RATIO_THR):
        reasons.append(f"COMMA_GAP {cc}vs{mwc}")

    if cl and min(wl) / cl < SHORT_DISTR_THR:
        reasons.append(f"SHORT_DISTR {min(wl)/cl:.2f}")

    return reasons


def audit_file(path):
    data = json.loads(path.read_text(encoding="utf-8"))
    flagged = []          # (q_number, reasons)
    longest = 0
    nblocks = 0
    for q in data.get("questions", []):
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            if len(opts) < 2:
                continue
            nblocks += 1
            correct = next((o for o in opts if o.get("correct")), None)
            if correct and len(correct["text"]) == max(len(o["text"]) for o in opts):
                longest += 1
            r = audit_block(opts)
            if r:
                flagged.append((q.get("q_number"), r))
    rate = longest / nblocks if nblocks else 0
    return flagged, nblocks, rate


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("-")]
    verbose = "-v" in sys.argv
    top = None
    if "--top" in sys.argv:
        i = sys.argv.index("--top")
        top = int(sys.argv[i + 1]) if i + 1 < len(sys.argv) else 25
        args = [a for a in args if a != str(top)]

    targets = []
    roots = [Path(a).resolve() for a in args] or [SEED]
    for r in roots:
        if r.is_dir():
            targets += sorted(r.rglob("*.json"))
        elif r.suffix == ".json":
            targets.append(r)

    rows = []
    for p in targets:
        try:
            flagged, nblocks, rate = audit_file(p)
        except Exception as e:  # noqa
            print(f"ERR  {p}: {e}", file=sys.stderr)
            continue
        rate_flag = nblocks >= 6 and rate > 0.5
        score = len(flagged) + (5 if rate_flag else 0)
        rows.append((score, p, flagged, nblocks, rate, rate_flag))
        if verbose and flagged:
            print(f"\n## {p.relative_to(REPO)}  blocks={nblocks} correct_longest={rate:.0%}")
            for qn, rs in flagged:
                print(f"   Q{qn}: {', '.join(rs)}")

    rows.sort(key=lambda x: -x[0])
    if top:
        rows = rows[:top]

    print(f"\n=== кандидаты на ручную правку (порог LEN_AVG>{LEN_AVG_THR}, "
          f"LEN_SPREAD>{LEN_SPREAD_THR}) ===")
    print(f"{'flags':>5} {'longest':>7}  file")
    total_flags = 0
    for score, p, flagged, nblocks, rate, rate_flag in rows:
        if not flagged and not rate_flag:
            continue
        total_flags += len(flagged)
        mark = " ⚠CORRECT_LONGEST_RATE" if rate_flag else ""
        print(f"{len(flagged):>5} {rate:>6.0%}  {p.relative_to(REPO)}{mark}")
    print(f"\nфайлов с флагами: {sum(1 for r in rows if r[2] or r[5])}; "
          f"всего флагов блоков: {total_flags}")
    print("Скрипт только находит кандидатов — финальное решение за человеком.")


if __name__ == "__main__":
    main()
