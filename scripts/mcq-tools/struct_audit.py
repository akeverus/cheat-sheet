#!/usr/bin/env python3
"""Сплошной аудит структурного паритета MCQ по всему корпусу.

Две метрики, обе считаются ровно так же, как в mcq-struct-report.py и
mcq-struct-scan.py, чтобы аудит и гейт не расходились:

1. КОРИДОР (абсолютный критерий приёмки). Блок вне коридора, если хоть один
   дистрактор выпал: длина в символах вне 0.85–1.15 от correct, число частей
   фразы не равно, код-спанов отличается больше чем на 1, слов вне 0.77–1.30.
2. ПРИЗНАК ВЫДАЁТ correct (сравнительный). Доля блоков, где correct — уникальный
   максимум по признаку. Случайный уровень 0.25: выше него признак работает
   подсказкой. НЕ считать «хоть один из признаков экстремален» — так случайный
   уровень около 0.94 и метрика бессмысленна.

  struct_audit.py <корень seed/mcq> [--csv out.csv]
"""
import json, os, re, sys

ROOT = sys.argv[1]
CSV = sys.argv[sys.argv.index("--csv") + 1] if "--csv" in sys.argv else None

cl = lambda t: 1 + t.count(",") + t.count("—") + t.count(";")
SP = lambda t: len(re.findall(r"`[^`]+`", t))
W = lambda t: len(re.findall(r"\w+", re.sub(r"`[^`]*`", "", t), flags=re.U))
DOT = lambda t: 1 if t.rstrip().endswith((".", "!", "?")) else 0
FEATS = {"симв": len, "частей": cl, "спанов": SP, "слов": W, "точка": DOT}


def audit(path):
    d = json.load(open(path, encoding="utf-8"))
    n = out = 0
    uniq = {k: 0 for k in FEATS}
    for q in d.get("questions", []):
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            cs = [o for o in opts if o.get("correct")]
            if len(opts) != 4 or len(cs) != 1:
                continue
            c, w = cs[0], [o for o in opts if not o.get("correct")]
            n += 1
            L, P, S, Wc = len(c["text"]), cl(c["text"]), SP(c["text"]), W(c["text"])
            for x in w:
                if not 0.85 <= len(x["text"]) / max(1, L) <= 1.15 \
                   or cl(x["text"]) != P \
                   or abs(SP(x["text"]) - S) > 1 \
                   or not 0.77 <= W(x["text"]) / max(1, Wc) <= 1.30:
                    out += 1
                    break
            for k, f in FEATS.items():
                vw = [f(x["text"]) for x in w]
                if f(c["text"]) > max(vw):
                    uniq[k] += 1
    return n, out, uniq


rows = []
for dp, _, fns in os.walk(ROOT):
    for fn in sorted(fns):
        if not fn.endswith(".json"):
            continue
        p = os.path.join(dp, fn)
        try:
            n, out, uniq = audit(p)
        except Exception as e:
            print(f"SKIP {p}: {e}")
            continue
        if n:
            rows.append((fn[:-5], n, out, uniq))

rows.sort(key=lambda r: -(r[2] / r[1]))
TN = sum(r[1] for r in rows)
TO = sum(r[2] for r in rows)
print(f"файлов: {len(rows)}  блоков: {TN}")
print(f"вне коридора: {TO} блоков ({TO/TN:.1%}); файлов без единого нарушения: "
      f"{sum(1 for r in rows if r[2] == 0)}")
print("\nпризнак уникально выделяет correct (случайный уровень 0.250):")
for k in FEATS:
    s = sum(r[3][k] for r in rows)
    print(f"  {k:<8} {s/TN:.3f}")
print("\nхудшие 12 файлов по доле блоков вне коридора:")
for slug, n, out, _ in rows[:12]:
    print(f"  {slug:<45} {out:>3}/{n:<3} ({out/n:.0%})")
if CSV:
    with open(CSV, "w", encoding="utf-8") as f:
        f.write("slug,blocks,out_of_corridor," + ",".join(FEATS) + "\n")
        for slug, n, out, uq in rows:
            f.write(f"{slug},{n},{out}," + ",".join(str(uq[k]) for k in FEATS) + "\n")
    print(f"\nполный список: {CSV}")
