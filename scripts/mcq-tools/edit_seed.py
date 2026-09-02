#!/usr/bin/env python3
"""Правка seed по префиксу: edit_seed.py <seed.json> <edits.json>
edits = [{"q":13,"prefix":"...","text":"..."}] либо
        [{"q":2,"label":"C","section":"source_of_confusion","old":"...","new":"..."}]"""
import json, sys
S, E = sys.argv[1], sys.argv[2]
d = json.load(open(S, encoding="utf-8")); Q = {q["q_number"]: q for q in d["questions"]}
n = 0
for e in json.load(open(E, encoding="utf-8")):
    opts = Q[e["q"]]["blocks"][e.get("block", 0)]["options"]
    if "section" in e:
        o = [x for x in opts if x["label"] == e["label"]][0]
        assert e["old"] in o["sections"][e["section"]], e
        o["sections"][e["section"]] = o["sections"][e["section"]].replace(e["old"], e["new"])
    else:
        for o in opts:
            if o["text"].startswith(e["prefix"]): o["text"] = e["text"]; break
        else: raise SystemExit(f"не найдено Q{e['q']}: {e['prefix']}")
    n += 1
json.dump(d, open(S, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
print("правок:", n)
