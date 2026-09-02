#!/usr/bin/env python3
"""Первые N символов тела каждого ## Q — для разметки углов в dup_note.
  qheads.py <md> [chars] [q1,q2,...]"""
import re, sys
t = open(sys.argv[1], encoding="utf-8").read()
n = int(sys.argv[2]) if len(sys.argv) > 2 else 220
only = set(int(x) for x in sys.argv[3].split(",")) if len(sys.argv) > 3 else None
parts = re.split(r"(?m)^(## Q\d+\..*)$", t)
for i in range(1, len(parts), 2):
    num = int(re.match(r"## Q(\d+)\.", parts[i]).group(1))
    if only and num not in only: continue
    body = re.sub(r"\s+", " ", parts[i + 1].strip())
    print(parts[i]); print("   ", body[:n], "\n")
