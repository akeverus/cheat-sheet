#!/usr/bin/env python3
"""Сверка blind-пиков с correct: picks.py <seed.json> <verdicts.json>"""
import json, sys
d = json.load(open(sys.argv[1], encoding="utf-8")); V = json.load(open(sys.argv[2], encoding="utf-8"))
corr = {q["q_number"]: [o["label"] for o in q["blocks"][0]["options"] if o["correct"]][0] for q in d["questions"]}
mm = [(v["q_number"], corr[v["q_number"]], v["picked_label"]) for v in V if v["picked_label"] != corr[v["q_number"]]]
low = [(v["q_number"], v["linkage_level"]) for v in V if v["linkage_level"] not in ("L3", "L4")]
print(f"вердиктов {len(V)} | несовпадений {len(mm)} {mm} | ниже L3 {low}")
print("L3:", [v["q_number"] for v in V if v["linkage_level"] == "L3"],
      "| не опровержимы:", [v["q_number"] for v in V if not v["all_wrong_refutable_from_this_Q"]],
      "| novel:", [v["q_number"] for v in V if v["novel_fact_required"]])
