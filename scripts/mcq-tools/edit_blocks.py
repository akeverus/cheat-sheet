#!/usr/bin/env python3
"""Правка авторских блоков по префиксу: edit_blocks.py <blocks.json> <edits.json>
edits = [{"q":13,"which":"correct"|"wrong","prefix":"...","text":"..."}]"""
import json, sys
P, E = sys.argv[1], sys.argv[2]
d = json.load(open(P, encoding="utf-8")); B = {b["q_number"]: b for b in d["blocks"]}
edits = json.load(open(E, encoding="utf-8"))
for e in edits:
    b = B[e["q"]]
    if e["which"] == "correct":
        assert b["correct_text"].startswith(e["prefix"]), e; b["correct_text"] = e["text"]
    else:
        for w in b["wrong"]:
            if w["text"].startswith(e["prefix"]): w["text"] = e["text"]; break
        else: raise SystemExit(f"не найден дистрактор Q{e['q']}: {e['prefix']}")
json.dump(d, open(P, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print("правок применено:", len(edits))
