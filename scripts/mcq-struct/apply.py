#!/usr/bin/env python3
"""Применяет переписанные дистракторы из journal.jsonl к сиду. correct не трогает.
  apply.py <seed.json> <journal.jsonl> [...]"""
import json, sys, re
S, journals = sys.argv[1], sys.argv[2:]
new = {}
for p in journals:
    for line in open(p, encoding="utf-8"):
        o = json.loads(line)
        if o.get("type") == "result":
            for b in (o.get("result") or {}).get("blocks", []):
                new[b["q_number"]] = b["wrong_texts"]
d = json.load(open(S, encoding="utf-8"))
applied, skipped = 0, []
for q in d["questions"]:
    wt = new.get(q["q_number"])
    if not wt: continue
    wrongs = [o for o in q["blocks"][0]["options"] if not o["correct"]]
    if len(wrongs) != len(wt): skipped.append(q["q_number"]); continue
    for o, t in zip(wrongs, wt): o["text"] = re.sub(r"\s+", " ", t.strip())
    applied += 1
json.dump(d, open(S, "w", encoding="utf-8"), ensure_ascii=False, indent=2); open(S, "a").write("\n")
print(f"блоков обновлено: {applied} | пропущено: {skipped or 'нет'}")
