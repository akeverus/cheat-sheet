#!/usr/bin/env python3
"""Сводка pair_verify_wf: pairs_collect.py <out.json> <journal.jsonl> [--notes]"""
import json, sys
out, journal = sys.argv[1], sys.argv[2]
R = []
for line in open(journal, encoding="utf-8"):
    o = json.loads(line)
    if o.get("type") == "result" and isinstance(o.get("result"), dict) and "q_a" in o["result"]:
        R.append(o["result"])
R.sort(key=lambda r: (r["q_a"], r["q_b"]))
json.dump(R, open(out, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
from collections import Counter
print("пар:", len(R), "| вердикты:", dict(Counter(r["verdict"] for r in R)))
for r in R:
    if r["verdict"] != "CLEAN" or r["answerable_from_b_alone"] or r["options_refuted_only_by_b"]:
        print(f"--- Q{r['q_a']}~Q{r['q_b']} {r['verdict']} pickA={r['picked_from_a']} fromB={r['answerable_from_b_alone']}/{r['picked_from_b']} "
              f"refutedOnlyByB={r['options_refuted_only_by_b']} shared={r['shared_vocabulary'][:6]}")
        if "--notes" in sys.argv: print("    ", r["note"][:600])
