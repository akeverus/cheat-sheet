#!/usr/bin/env python3
"""Тексты блоков с метриками: show.py <blocks.json|seed.json> <q1,q2,...>"""
import json, sys, os
sys.path.insert(0, os.path.dirname(__file__)); from metrics import *
d = json.load(open(sys.argv[1], encoding="utf-8"))
want = set(int(x) for x in sys.argv[2].split(","))
items = []
if "blocks" in d:
    items = [(b["q_number"], b["correct_text"], [("", w["text"]) for w in b["wrong"]]) for b in d["blocks"]]
else:
    for q in d["questions"]:
        o = sorted(q["blocks"][0]["options"], key=lambda x: x["order"])
        c = [x for x in o if x["correct"]][0]
        items.append((q["q_number"], c["text"], [(x["label"] + ": ", x["text"]) for x in o if not x["correct"]]))
for n, c, ws in items:
    if n not in want: continue
    print(f"== Q{n} эталон {len(c)}c/{cl(c)}ч/{sp(c)}сп/{gw(c)}|{W(c)}сл: {c}")
    for lb, t in ws:
        print(f"   {len(t)}c({len(t)/len(c):.2f}) {cl(t)}ч {sp(t)}сп {gw(t)}|{W(t)}сл({gw(t)/gw(c):.2f}|{W(t)/max(W(c),1):.2f}) :: {lb}{t}")
