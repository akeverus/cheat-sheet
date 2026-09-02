#!/usr/bin/env python3
"""Блоки, где correct — единственный экстремум: tellblocks.py <seed.json> [симв|слов|частей|спанов]"""
import json, sys, os
sys.path.insert(0, os.path.dirname(__file__)); from metrics import *
F = {"симв": len, "слов": W, "частей": cl, "спанов": sp}
f = F[sys.argv[2] if len(sys.argv) > 2 else "симв"]
d = json.load(open(sys.argv[1], encoding="utf-8")); hi = lo = 0
for q in d["questions"]:
    o = sorted(q["blocks"][0]["options"], key=lambda x: x["order"])
    c = [x for x in o if x["correct"]][0]; vw = [f(x["text"]) for x in o if not x["correct"]]; vc = f(c["text"])
    if vc > max(vw): hi += 1; print(f"Q{q['q_number']} MAX correct={vc} дистракторы={sorted(vw)} :: {c['text'][:70]}")
    elif vc < min(vw): lo += 1; print(f"Q{q['q_number']} MIN correct={vc} дистракторы={sorted(vw)} :: {c['text'][:70]}")
print(f"--- уникальный максимум: {hi}, уникальный минимум: {lo} из {len(d['questions'])}")
