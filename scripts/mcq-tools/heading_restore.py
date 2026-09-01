#!/usr/bin/env python3
"""HEADING-RESTORE: замена заголовков + ассерт неизменности тел + пересборка TOC.
  heading_restore.py <file.md> <mapping.json>   # {"1": "текст без 'Q1.' и '(!)'"}"""
import json, re, sys
MD, MAP = sys.argv[1], sys.argv[2]
titles = {int(k): v for k, v in json.load(open(MAP, encoding="utf-8")).items()}
txt = orig = open(MD, encoding="utf-8").read()
def bodies(t):
    hs = [(m.start(), m.end(), int(m.group(1))) for m in re.finditer(r"^##\s*Q(\d+)\.[^\n]*\n", t, re.M)]
    return {n: t[e:(hs[i+1][0] if i+1 < len(hs) else len(t))] for i, (s, e, n) in enumerate(hs)}
changed = []
def repl(m):
    n = int(m.group(1)); bang = m.group(2) or ""
    if n not in titles: return m.group(0)
    changed.append(n); return f"## Q{n}. {bang}{titles[n]}"
txt = re.sub(r"^##\s*Q(\d+)\.\s*(\(!\)\s*)?[^\n]*", repl, txt, flags=re.M)
if re.search(r"> \[!mcq\]|Tier 1", txt, re.I): sys.exit("ABORT: MCQ-callout detected")
if len(changed) != len(titles): sys.exit(f"ABORT: заменено {len(changed)}, ожидалось {len(titles)}")
if bodies(orig) != bodies(txt): sys.exit("ABORT: изменились тела Q")
heads = {int(m.group(2)): m.group(1).strip() for m in re.finditer(r"^##\s*(Q(\d+)\.[^\n]*)$", txt, re.M)}
anchor = lambda h: re.sub(r"[^\w\s-]", "", h.lower(), flags=re.U).replace(" ", "-")
fixed = [0]
def toc(m):
    n = int(m.group(2))
    if n not in heads: return m.group(0)
    new = f"- [{heads[n]}](#{anchor(heads[n])})"
    if new != m.group(0): fixed[0] += 1
    return new
txt = re.sub(r"^- \[(Q(\d+)\.[^\]]*)\]\(#[^)]*\)$", toc, txt, flags=re.M)
open(MD, "w", encoding="utf-8").write(txt)
print(f"заголовков восстановлено: {len(changed)} | TOC синхронизировано: {fixed[0]}")
print(f"тела: НЕ ИЗМЕНЕНЫ (проверено) | len: {len(orig)} -> {len(txt)} ({len(txt)-len(orig):+d})")
