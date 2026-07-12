#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Выравнивание визуальной формы MCQ-опций (ROUND-8 form-tell fixer).

Снимает угадываемость correct по:
- плотности `backticks` (code-span parity);
- маркерам абсолютности (ABSOLUTE_MARKER_GAP);
- «каталоговым» перечислениям в correct (semi/clauses).

Меняет только поле option.text; sections не трогает.

НЕ закрывает колонку SKEL / llm_review.skel: единый surface skeleton всех 4
вариантов проверяет LLM (§18 / §31). Грубый pre-check:
`python3 scripts/mcq-skel-gate.py <json>` — зелёный ≠ SKEL PASS. После любого
скриптового баланса обязателен повтор §31.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from copy import deepcopy

BACKTICK = re.compile(r"`[^`]+`")
ABS = re.compile(
    r"(?:всегда|никогда|только|исключительно|люб\w+|навсегда|во\s+всех\s+случаях"
    r"|единственн\w+|полностью|гарантированн\w+|обязательно|ни\s+в\s+коем\s+случае)",
    re.IGNORECASE,
)
# Порядок: более длинные фразы первыми
ABS_SOFTEN: list[tuple[re.Pattern[str], str]] = [
    (re.compile(r"ни\s+в\s+коем\s+случае", re.I), "крайне редко"),
    (re.compile(r"во\s+всех\s+случаях", re.I), "во многих случаях"),
    (re.compile(r"гарантированн\w+", re.I), "обычно"),
    (re.compile(r"исключительно", re.I), "в первую очередь"),
    (re.compile(r"единственн\w+", re.I), "основной"),
    (re.compile(r"обязательно", re.I), "как правило"),
    (re.compile(r"полностью", re.I), "в основном"),
    (re.compile(r"навсегда", re.I), "надолго"),
    (re.compile(r"всегда", re.I), "часто"),
    (re.compile(r"никогда", re.I), "редко"),
    (re.compile(r"только", re.I), "преимущественно"),
]

INNER_PAREN_BT = re.compile(r"`[^`]+`\s*\([^)]*`[^`]+`[^)]*\)")


def words(t: str) -> int:
    t = BACKTICK.sub(" CODE ", t or "")
    return len(re.findall(r"[A-Za-zА-Яа-яЁё0-9_]+", t))


def bt_count(t: str) -> int:
    return len(BACKTICK.findall(t or ""))


def abs_count(t: str) -> int:
    return len(ABS.findall(t or ""))


def soften_one_absolute(text: str) -> tuple[str, bool]:
    for pat, repl in ABS_SOFTEN:
        m = pat.search(text)
        if m:
            return text[: m.start()] + repl + text[m.end() :], True
    return text, False


def trim_backtick_catalog(text: str, target: int) -> str:
    """Убирает вложенные (`Type` (`full.name`)) и лишние элементы списка."""
    out = INNER_PAREN_BT.sub(lambda m: BACKTICK.findall(m.group(0))[0], text)
    spans = BACKTICK.findall(out)
    if len(spans) <= target:
        return out
    # Схлопнуть хвост перечисления после последнего «:»
    if ":" in out:
        head, tail = out.split(":", 1)
        parts = [p.strip() for p in re.split(r",|;", tail) if p.strip()]
        if len(parts) > target:
            kept = ", ".join(parts[:target])
            if len(parts) > target:
                kept += " и др."
            return head + ": " + kept
    # Fallback: unwrap лишние backticks с конца
    while bt_count(out) > target:
        last = out.rfind("`")
        if last < 0:
            break
        start = out.rfind("`", 0, last)
        if start < 0:
            break
        plain = out[start + 1 : last]
        out = out[:start] + plain + out[last + 1 :]
    return out


def shorten_catalog_correct(text: str) -> str:
    """Сокращает correct с несколькими `;` или длинным перечислением."""
    if words(text) < 12:
        return text  # не сжимать уже короткие варианты
    if text.count(";") >= 2:
        parts = [p.strip() for p in text.split(";") if p.strip()]
        if len(parts) >= 2:
            return parts[0] + "; " + parts[1].split(",")[0].strip()
    if text.count(",") >= 4 and ":" in text:
        head, tail = text.split(":", 1)
        items = [x.strip() for x in tail.split(",") if x.strip()]
        if len(items) > 3:
            return head + ": " + ", ".join(items[:3]) + " и другие примеры"
    return text


def boost_wrong_backticks(text: str, need: int) -> str:
    """Добавляет 1–2 backtick в wrong, если их мало (без изменения смысла)."""
    if bt_count(text) >= need:
        return text
    # Имена паттернов / API без backticks
    for name in (
        "Strategy", "Singleton", "Factory", "Observer", "Decorator", "Proxy",
        "Iterator", "Builder", "Adapter", "Template Method", "Flyweight",
        "ConcurrentHashMap", "volatile", "Future", "ExecutorService",
        "SELECT", "JOIN", "INDEX", "PostgreSQL", "Spring", "JPA",
    ):
        if name in text and f"`{name}`" not in text and bt_count(text) < need:
            text = text.replace(name, f"`{name}`", 1)
            if bt_count(text) >= need:
                return text
    return text


def block_metrics(options: list[dict]) -> dict:
    corr = next(o for o in options if o.get("correct"))
    wrong = [o for o in options if not o.get("correct")]
    cw = words(corr["text"])
    ww = [words(o["text"]) for o in wrong]
    cbt = bt_count(corr["text"])
    wbt = [bt_count(o["text"]) for o in wrong]
    cab = abs_count(corr["text"])
    wab = sum(abs_count(o["text"]) for o in wrong)
    return {
        "corr": corr,
        "wrong": wrong,
        "cw": cw,
        "ww": ww,
        "cbt": cbt,
        "wbt": wbt,
        "max_wbt": max(wbt) if wbt else 0,
        "cab": cab,
        "wab": wab,
        "max_ww": max(ww) if ww else 0,
    }


def balance_block(options: list[dict]) -> tuple[list[dict], list[str]]:
    opts = deepcopy(options)
    notes: list[str] = []
    m = block_metrics(opts)
    corr = next(o for o in opts if o.get("correct"))
    wrong = [o for o in opts if not o.get("correct")]

    # 1) Backtick parity
    if m["cbt"] > m["max_wbt"] + 1:
        old = corr["text"]
        target = max(m["max_wbt"] + 1, 3)
        corr["text"] = trim_backtick_catalog(shorten_catalog_correct(corr["text"]), target)
        if corr["text"] != old:
            notes.append(f"trim-bt {m['cbt']}→{bt_count(corr['text'])}")
        m = block_metrics(opts)

    if m["cbt"] > m["max_wbt"] + 1:
        for w in wrong:
            old = w["text"]
            w["text"] = boost_wrong_backticks(w["text"], m["max_wbt"] + 1)
            if w["text"] != old:
                notes.append(f"boost-bt {w['label']}")
        m = block_metrics(opts)

    # 2) Absolute marker gap (soften wrong, не трогаем correct если уже есть abs)
    if m["cab"] == 0 and m["wab"] >= 2:
        for w in wrong:
            if abs_count(w["text"]) > 0:
                new, changed = soften_one_absolute(w["text"])
                if changed:
                    w["text"] = new
                    notes.append(f"soften-abs {w['label']}")
        m = block_metrics(opts)

    # 3) Length: correct lone-longest
    if m["cw"] >= m["max_ww"] + 4 and m["cw"] == max([m["cw"]] + m["ww"]):
        old = corr["text"]
        corr["text"] = shorten_catalog_correct(corr["text"])
        if len(corr["text"]) > len(old) * 0.85 and ";" in corr["text"]:
            parts = corr["text"].split(";")
            corr["text"] = parts[0].strip() + "."
        elif len(corr["text"]) >= len(old):
            # обрезать второе предложение после точки
            sentences = re.split(r"(?<=[.!?])\s+", corr["text"])
            if len(sentences) > 1:
                corr["text"] = sentences[0]
        if corr["text"] != old:
            notes.append("trim-len-correct")

    return opts, notes


def process_file(path: str, dry_run: bool = False) -> dict:
    data = json.load(open(path, encoding="utf-8"))
    total_notes: list[str] = []
    for q in data.get("questions", []):
        for b in q.get("blocks", []):
            new_opts, notes = balance_block(b["options"])
            if notes:
                total_notes.append(f"Q{q['q_number']}: {', '.join(notes)}")
                if not dry_run:
                    b["options"] = new_opts
    if not dry_run:
        with open(path, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
            f.write("\n")
    return {"path": path, "changes": len(total_notes), "notes": total_notes}


def main() -> int:
    ap = argparse.ArgumentParser(description="Balance MCQ option visual form")
    ap.add_argument("files", nargs="+", help="MCQ JSON files")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    for p in args.files:
        r = process_file(p, dry_run=args.dry_run)
        print(f"{p}: {r['changes']} blocks touched")
        for n in r["notes"][:30]:
            print(" ", n)
        if r["changes"] > 30:
            print(f"  ... +{r['changes'] - 30} more")
    return 0


if __name__ == "__main__":
    sys.exit(main())
