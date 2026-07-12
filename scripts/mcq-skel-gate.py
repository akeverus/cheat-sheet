#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""MCQ surface-skeleton PRE-CHECK (SKEL) — pass/fail, read-only.

Ловит грубый mismatch каркаса у четырёх option.text в блоке:
  - класс зачина (backtick-lead / in-API / prose);
  - число предложений (max−min > 1);
  - наличие `;` (каталог vs проза);
  - bucket плотности `` `code` `` (0 / 1–2 / 3+).

WARN (не валят exit, печатаются):
  - наличие `:` / `—` / `(` разъехалось между вариантами.

═══════════════════════════════════════════════════════════════════════════
ВАЖНО — скрипт НЕ принимает колонку SKEL
═══════════════════════════════════════════════════════════════════════════
  • Exit 0  ≠  право ставить SKEL=✅ / llm_review.skel=true / PAR / FINAL.
  • Exit 1  =  pre-check красный: блок надо чинить или объяснить в notes.
  • Приёмка скелета — только LLM (§18 / §31 PROMPT_PLAN_INTERVIEW.md):
    зачин, клаузы, пунктуационный каркас, класс backticks/списков, тип ответа.
  • Скрипт не видит semantic skeleton (определение vs сравнение vs API-пара)
    и «почти тот же каркас» глазами. LLM-проверка вручную обязательна
    независимо от цвета этого гейта.

Использование
-------------
  python3 scripts/mcq-skel-gate.py <json...>
  python3 scripts/mcq-skel-gate.py --verbose <json>
  python3 scripts/mcq-skel-gate.py --json <json...>
  python3 scripts/mcq-skel-gate.py --selftest

Exit 0 — нет HARD fails; exit 1 — есть HARD; exit 2 — usage/IO.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

BACKTICK = re.compile(r"`[^`]*`")
ABBREV = {"т.д", "т.е", "т.к", "т.п", "др", "напр", "рис", "см", "vs", "etc", "e.g", "i.e"}


def strip_code(t: str) -> str:
    return BACKTICK.sub(" ", t or "")


def sentences(t: str) -> int:
    plain = strip_code(t)
    parts = re.split(r"[.!?…]+(?:\s|$)", plain)
    cnt = 0
    for p in parts:
        p = p.strip()
        if not p:
            continue
        last = p.split()[-1].strip(".,:;()").lower() if p.split() else ""
        if last in ABBREV:
            continue
        cnt += 1
    return max(1, cnt) if (t or "").strip() else 0


def code_spans(t: str) -> int:
    return len(BACKTICK.findall(t or ""))


def code_bucket(t: str) -> int:
    n = code_spans(t)
    if n == 0:
        return 0
    if n <= 2:
        return 1
    return 2


def opening_class(t: str) -> str:
    s = (t or "").strip()
    if not s:
        return "empty"
    if s.startswith("`"):
        return "btick"
    if re.match(r"^(?:В|In)\s+API\b", s, re.I):
        return "in_api"
    if re.match(r"^(?:В|In)\s+JEP\b", s, re.I):
        return "in_jep"
    return "prose"


def option_sig(t: str) -> dict:
    return {
        "open": opening_class(t),
        "sent": sentences(t),
        "semi": 1 if ";" in (t or "") else 0,
        "colon": 1 if ":" in (t or "") else 0,
        "dash": 1 if ("—" in (t or "") or " – " in (t or "")) else 0,
        "paren": 1 if "(" in (t or "") else 0,
        "code_bucket": code_bucket(t),
        "code": code_spans(t),
    }


def block_check(opts: list[dict]) -> dict:
    """Вернуть hard/warn списки строк-причин и per-label signatures."""
    texts = []
    labels = []
    for o in opts:
        labels.append(str(o.get("label") or "?"))
        texts.append(o.get("text") or "")
    sigs = [option_sig(t) for t in texts]
    hard: list[str] = []
    warn: list[str] = []

    opens = [s["open"] for s in sigs]
    if len(set(opens)) > 1:
        hard.append(f"opening_class mismatch: {dict(zip(labels, opens))}")

    sents = [s["sent"] for s in sigs]
    if max(sents) - min(sents) > 1:
        hard.append(f"sentence_count max-min>1: {dict(zip(labels, sents))}")

    semis = [s["semi"] for s in sigs]
    if len(set(semis)) > 1:
        hard.append(f"semicolon_presence mismatch: {dict(zip(labels, semis))}")

    codes = [s["code_bucket"] for s in sigs]
    if max(codes) - min(codes) > 1:
        hard.append(
            f"code_bucket max-min>1: {dict(zip(labels, codes))} "
            f"(raw={dict(zip(labels, [s['code'] for s in sigs]))})"
        )

    for key, title in (("colon", "colon_presence"), ("dash", "dash_presence"), ("paren", "paren_presence")):
        vals = [s[key] for s in sigs]
        if len(set(vals)) > 1:
            warn.append(f"{title} mismatch: {dict(zip(labels, vals))}")

    return {
        "hard": hard,
        "warn": warn,
        "sigs": {lab: sig for lab, sig in zip(labels, sigs)},
        "pass": not hard,
    }


def iter_blocks(data: dict):
    for q in data.get("questions", []):
        qn = q.get("q_number")
        for b in q.get("blocks", []):
            opts = b.get("options") or []
            if opts:
                yield qn, b.get("block_idx", 0), opts


def analyze_file(path: Path) -> dict:
    data = json.loads(path.read_text(encoding="utf-8"))
    blocks = []
    hard_n = warn_n = 0
    for qn, bi, opts in iter_blocks(data):
        if len(opts) != 4:
            blocks.append(
                {
                    "q_number": qn,
                    "block_idx": bi,
                    "pass": False,
                    "hard": [f"expected 4 options, got {len(opts)}"],
                    "warn": [],
                    "sigs": {},
                }
            )
            hard_n += 1
            continue
        r = block_check(opts)
        if r["hard"]:
            hard_n += 1
        if r["warn"]:
            warn_n += 1
        blocks.append(
            {
                "q_number": qn,
                "block_idx": bi,
                "pass": r["pass"],
                "hard": r["hard"],
                "warn": r["warn"],
                "sigs": r["sigs"],
            }
        )
    return {
        "file": str(path),
        "blocks": len(blocks),
        "hard_fails": hard_n,
        "warn_blocks": warn_n,
        "pass": hard_n == 0,
        "results": blocks,
        "reminder": (
            "PRE-CHECK ONLY: exit color does not authorize SKEL=✅; "
            "LLM §18/§31 surface-skeleton review remains mandatory."
        ),
    }


def print_human(report: dict, verbose: bool) -> None:
    status = "PASS" if report["pass"] else "FAIL"
    print(f"[{status}] {report['file']}  blocks={report['blocks']}  "
          f"hard_fails={report['hard_fails']}  warn_blocks={report['warn_blocks']}")
    print(f"  ! {report['reminder']}")
    for b in report["results"]:
        if not b["hard"] and not (verbose and b["warn"]):
            if verbose and b["pass"]:
                continue
            if not b["hard"] and not b["warn"]:
                continue
        if b["hard"]:
            print(f"  HARD Q{b['q_number']}/b{b['block_idx']}:")
            for h in b["hard"]:
                print(f"    - {h}")
            if verbose:
                print(f"    sigs={b['sigs']}")
        if b["warn"] and (verbose or b["hard"]):
            print(f"  WARN Q{b['q_number']}/b{b['block_idx']}:")
            for w in b["warn"]:
                print(f"    - {w}")


def selftest() -> int:
    ok = True

    # PASS: same skeleton (Q56-style)
    same = [
        {"label": "A", "text": "В API JEP 453: Foo — x; Bar — y."},
        {"label": "B", "text": "В API JEP 453: Foo — a; Bar — b."},
        {"label": "C", "text": "В API JEP 453: Foo — c; Bar — d."},
        {"label": "D", "text": "В API JEP 453: Foo — e; Bar — f."},
    ]
    r = block_check(same)
    if not r["pass"]:
        print("SELFTEST FAIL: expected PASS on shared skeleton", r)
        ok = False

    # FAIL: catalog correct vs prose wrongs
    mixed = [
        {"label": "A", "text": "`Foo` — делает X; `Bar` — делает Y."},
        {"label": "B", "text": "Вариант просто описывает поведение без списка."},
        {"label": "C", "text": "Другой короткий тезис без точки с запятой."},
        {"label": "D", "text": "Ещё одна прозаическая формулировка."},
    ]
    r = block_check(mixed)
    if r["pass"]:
        print("SELFTEST FAIL: expected HARD on catalog vs prose", r)
        ok = False
    else:
        joined = " ".join(r["hard"])
        if "opening_class" not in joined and "semicolon" not in joined:
            print("SELFTEST FAIL: expected opening/semi hard reasons", r)
            ok = False

    # FAIL: sentence count
    sents = [
        {"label": "A", "text": "Один тезис."},
        {"label": "B", "text": "Первое. Второе. Третье."},
        {"label": "C", "text": "Коротко."},
        {"label": "D", "text": "Тоже коротко."},
    ]
    r = block_check(sents)
    if r["pass"] or not any("sentence_count" in h for h in r["hard"]):
        print("SELFTEST FAIL: expected sentence hard", r)
        ok = False

    print("SELFTEST", "OK" if ok else "FAILED")
    print("Reminder: script green never replaces LLM SKEL review.")
    return 0 if ok else 1


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="MCQ SKEL surface-skeleton PRE-CHECK (LLM still required)")
    ap.add_argument("paths", nargs="*", type=Path, help="MCQ JSON files")
    ap.add_argument("--verbose", "-v", action="store_true")
    ap.add_argument("--json", action="store_true", help="machine-readable JSON report")
    ap.add_argument("--selftest", action="store_true")
    args = ap.parse_args(argv)

    if args.selftest:
        return selftest()
    if not args.paths:
        ap.print_help()
        return 2

    reports = []
    any_fail = False
    for path in args.paths:
        if not path.is_file():
            print(f"ERROR: not a file: {path}", file=sys.stderr)
            return 2
        rep = analyze_file(path)
        reports.append(rep)
        if not rep["pass"]:
            any_fail = True
        if args.json:
            continue
        print_human(rep, args.verbose)

    if args.json:
        print(json.dumps(reports if len(reports) > 1 else reports[0], ensure_ascii=False, indent=2))

    if not args.json:
        print()
        print("SKEL acceptance = LLM only. This script is a pre-check helper.")
    return 1 if any_fail else 0


if __name__ == "__main__":
    raise SystemExit(main())
