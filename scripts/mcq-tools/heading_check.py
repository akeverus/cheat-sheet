#!/usr/bin/env python3
"""Проверка подмены заголовков коммитом 8b29f4a3: идентичны ли тела, что было до.
  heading_check.py <file.md> [--sha 8b29f4a3]"""
import subprocess, sys, re
MD = sys.argv[1]
SHA = sys.argv[sys.argv.index("--sha") + 1] if "--sha" in sys.argv else "8b29f4a3"
old = subprocess.run(["git", "show", f"{SHA}^:{MD}"], capture_output=True, text=True).stdout
if not old: sys.exit(f"файл не существовал до {SHA} — подмены нет")
new = open(MD, encoding="utf-8").read()
def parse(t):
    hs = [(m.start(), m.end(), int(m.group(1)), m.group(0).strip())
          for m in re.finditer(r"^##\s*Q(\d+)\.[^\n]*\n", t, re.M)]
    return ({n: h for _, _, n, h in hs},
            {n: t[e:(hs[i+1][0] if i+1 < len(hs) else len(t))] for i, (s, e, n, _) in enumerate(hs)})
ho, bo = parse(old); hn, bn = parse(new)
same = [n for n in bo if bo.get(n) == bn.get(n)]
print(f"Q было: {len(bo)}, стало: {len(bn)}")
print(f"ТЕЛА ИДЕНТИЧНЫ: {len(same)}/{len(bo)}" +
      ("  → безопасно восстанавливать заголовки" if len(same) == len(bo) else "  ⚠ тела менялись"))
ch = [n for n in ho if ho.get(n) != hn.get(n)]
print(f"заголовков подменено: {len(ch)}")
for n in sorted(ch)[:60]:
    print(f"\nQ{n} БЫЛО: {ho[n]}\nQ{n} СТАЛО: {hn[n]}")
