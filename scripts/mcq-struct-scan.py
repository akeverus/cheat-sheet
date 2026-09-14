#!/usr/bin/env python3
"""Визуальный анти-телл: отличается ли correct от дистракторов ГРАММАТИЧЕСКИ.

Штатные гейты (parity, anti-tell) считают СЛОВА после вычёркивания backtick-спанов
и потому слепы к тому, что видит глаз: длине в символах и числу частей фразы.
Этот скан меряет именно визуальные признаки.

  mcq-struct-scan.py <seed.json> [...]              — агрегат по файлам
  mcq-struct-scan.py --per-file <seed.json> [...]   — рейтинг файлов по худшей оси
"""
import json, re, sys
from collections import defaultdict

NEG = re.compile(r"\b(не|нет|ни|нельзя|невозможно|без|вместо)\b", re.I)
CONTRAST = re.compile(r"(\bа\b|\bно\b|\bзато\b|тогда как|в отличие|—)", re.I)

FEATS = {
    "clauses":      lambda t: 1 + t.count(",") + t.count("—") + t.count(";"),
    "chars":        lambda t: len(t),
    "commas":       lambda t: t.count(","),
    "contrast":     lambda t: len(CONTRAST.findall(t)),
    "dash":         lambda t: t.count("—"),
    "negation":     lambda t: len(NEG.findall(t)),
    "spans":        lambda t: len(re.findall(r"`[^`]+`", t)),
    "starts_latin": lambda t: 1 if re.match(r"^[`A-Za-z@]", t.strip()) else 0,
    "final_dot":    lambda t: 1 if t.rstrip().endswith(".") else 0,
}
THRESHOLD = 0.28   # Диагностический порог, не статистическое доказательство утечки.


def measure(path):
    """-> (число блоков, {признак:direction -> накопленный guess})"""
    with open(path, encoding="utf-8") as source:
        d = json.load(source)
    tot, hits, sep = 0, defaultdict(float), defaultdict(int)
    for q in d["questions"]:
        for b in q["blocks"]:
            o = b["options"]
            ci = [i for i, x in enumerate(o) if x["correct"]][0]
            tot += 1
            for name, f in FEATS.items():
                v = [f(x["text"]) for x in o]
                for direction, val in (("max", max(v)), ("min", min(v))):
                    win = [i for i, x in enumerate(v) if x == val]
                    key = f"{name}:{direction}"
                    if ci in win:
                        hits[key] += 1 / len(win)
                        if len(win) == 1:
                            sep[key] += 1
    return tot, hits, sep


def main(argv):
    per_file = "--per-file" in argv
    paths = [a for a in argv if a != "--per-file"]
    if per_file:
        rows = []
        for p in paths:
            tot, hits, _ = measure(p)
            if not tot:
                continue
            k, v = max(hits.items(), key=lambda kv: kv[1]) if hits else ("-", 0)
            rows.append((v / tot, k, tot, p.split("/")[-1]))
        for r in sorted(rows, reverse=True)[:30]:
            print(f"{r[0]:.3f}  {r[1]:16} {r[2]:>3}бл  {r[3]}")
        print(f"файлов: {len(rows)} | >0.40: {sum(1 for r in rows if r[0] > 0.40)}"
              f" | >0.50: {sum(1 for r in rows if r[0] > 0.50)}")
        return
    tot, hits, sep = 0, defaultdict(float), defaultdict(int)
    for p in paths:
        t, h, s = measure(p)
        tot += t
        for k, v in h.items(): hits[k] += v
        for k, v in s.items(): sep[k] += v
    if not tot:
        raise ValueError("не проверен ни один блок")
    print(f"блоков: {tot}   (случайный уровень = 0.250, диагностический порог = {THRESHOLD})")
    worst = [(k, v / tot) for k, v in hits.items() if v / tot >= THRESHOLD]
    for k, rate in sorted(worst, key=lambda x: -x[1]):
        print(f"{k:22} {rate:7.3f}   уникально выделяет correct: {sep[k]:>4} ({sep[k]/tot:.1%})")
    if not worst:
        print("нет превышений выбранного порога; отсутствие утечек НЕ доказано")
    print("Диагностика нескольких осей на том же наборе: нужны независимая выборка и LLM-ревью.")


if __name__ == "__main__":
    main(sys.argv[1:])
