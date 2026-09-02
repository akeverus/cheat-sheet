#!/usr/bin/env python3
"""Проставить sha в манифест и сайдкар, показать курсор: set_sha.py <slug> <sha>"""
import json, sys
slug, sha = sys.argv[1], sys.argv[2]
P = "docs/mcq-quality/linkage-reset-manifest.json"
m = json.load(open(P, encoding="utf-8")); seq = m["files"] if isinstance(m, dict) and "files" in m else m
lst = seq if isinstance(seq, list) else list(seq.values())
for f in lst:
    if f.get("topic_slug") == slug: f["audited"]["commit"] = sha
json.dump(m, open(P, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
S = f"docs/mcq-quality/reviews/{slug}.json"
d = json.load(open(S, encoding="utf-8")); d["commit"] = sha
json.dump(d, open(S, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
todo = sorted([f for f in lst if f.get("status") == "TODO"], key=lambda f: f.get("priority_rank", 10**9))
other = {f.get("status") for f in lst} - {"TODO", "AUDITED"}
print("AUDITED:", sum(1 for f in lst if f.get("status") == "AUDITED"), "| TODO:", len(todo), "| прочие статусы:", other or "нет")
n = todo[0]; print("следующий:", n["topic_slug"], "ранг", n["priority_rank"], "блоков", n["baseline"]["blocks"], "|", n["source_file"])
