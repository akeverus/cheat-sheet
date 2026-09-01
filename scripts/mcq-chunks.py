#!/usr/bin/env python3
"""Границы чанков для Workflow + выгрузка question_text.
  mcq-chunks.py <source.md> <per_chunk> [--qt out.json]"""
import re, sys, json
MD, PER = sys.argv[1], int(sys.argv[2])
L = open(MD, encoding="utf-8").read().split("\n")
qs = [(i + 1, int(m.group(1)), m.group(2)) for i, l in enumerate(L)
      if (m := re.match(r"^##\s*Q(\d+)\.\s*(?:\(!\)\s*)?(.+?)\s*$", l))]
pos = {n: ln for ln, n, _ in qs}
nums = sorted(pos)
chunks = []
for i in range(0, len(nums), PER):
    grp = nums[i:i + PER]; lo, hi = grp[0], grp[-1]
    start, end = pos[lo], pos.get(hi + 1, len(L) + 1)
    chunks.append({"label": f"Q{lo}-{hi}", "lo": lo, "hi": hi,
                   "off": max(1, start - 2), "lim": end - start + 3})
print(json.dumps(chunks, ensure_ascii=False))
print(f"# Q: {len(nums)}, чанков: {len(chunks)}", file=sys.stderr)
if "--qt" in sys.argv:
    json.dump({n: t for _, n, t in qs}, open(sys.argv[sys.argv.index("--qt") + 1], "w",
              encoding="utf-8"), ensure_ascii=False, indent=1)
