#!/usr/bin/env python3
"""question_text из заголовков ## Q: qt.py <md> <out.json>"""
import re, json, sys
qt = {}
for m in re.finditer(r"(?m)^## Q(\d+)\.\s*(.+)$", open(sys.argv[1], encoding="utf-8").read()):
    qt[m.group(1)] = re.sub(r"^\(!\)\s*", "", m.group(2).strip())
json.dump(qt, open(sys.argv[2], "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print(len(qt), "заголовков ->", sys.argv[2])
