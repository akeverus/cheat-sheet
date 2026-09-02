#!/usr/bin/env python3
"""Выгрузка НЕПОМЕЧЕННЫХ опций для source-blind верификации (§32/§79).
  blind.py <seed.json> <out.json>"""
import json, sys
d = json.load(open(sys.argv[1], encoding="utf-8"))
out = []
for q in d["questions"]:
    for b in q["blocks"]:
        out.append({"q_number": q["q_number"], "block_idx": b["block_idx"],
                    "options": [{"label": o["label"], "text": o["text"]}
                                for o in sorted(b["options"], key=lambda x: x["order"])]})
json.dump(out, open(sys.argv[2], "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print(f"blind: {len(out)} блоков -> {sys.argv[2]}")
