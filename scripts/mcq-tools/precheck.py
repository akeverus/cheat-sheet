#!/usr/bin/env python3
"""Предпроверка §99 ПО АВТОРСКИМ БЛОКАМ, до build.py: precheck.py <blocks.json>
Считает ОБА счёта слов (split и \\w+) — гейты расходятся."""
import json, sys, os
sys.path.insert(0, os.path.dirname(__file__)); from metrics import *
B = json.load(open(sys.argv[1], encoding="utf-8"))["blocks"]
bad = tell = 0
for b in B:
    c = b["correct_text"]
    for w in b["wrong"]:
        t = w["text"]
        if not in_corridor(c, t):
            bad += 1
            print(f"Q{b['q_number']} эталон {len(c)}c/{cl(c)}ч/{sp(c)}сп/{gw(c)}|{W(c)}сл | "
                  f"{len(t)}c({len(t)/len(c):.2f}) {cl(t)}ч {sp(t)}сп {gw(t)}|{W(t)}сл({gw(t)/gw(c):.2f}|{W(t)/max(W(c),1):.2f}) :: {t}")
    for msg in tells([c] + [w["text"] for w in b["wrong"]]):
        tell += 1; print(f"Q{b['q_number']} ТЕЛЛЬ {msg}")
print(f"--- блоков={len(B)} вне коридора={bad} из {3*len(B)} | визуальных теллей={tell}")
