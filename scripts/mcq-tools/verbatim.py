#!/usr/bin/env python3
"""Дословность опции относительно тела своего ## Q: длина самого длинного общего
непрерывного отрезка слов, делённая на число слов опции.
  verbatim.py <seed.json> <source.md> [порог]
Печатает блоки, где correct дословнее порога И дословнее лучшего дистрактора."""
import json, re, sys

SEED, MD = sys.argv[1], sys.argv[2]
THR = float(sys.argv[3]) if len(sys.argv) > 3 else 0.25

def words(t):
    t = re.sub(r"`([^`]*)`", r"\1", t.lower())
    return re.findall(r"[a-zа-яё0-9@._-]+", t)

def lcs_run(a, b):
    """Длина самого длинного общего непрерывного отрезка."""
    if not a or not b:
        return 0
    prev = [0] * (len(b) + 1)
    best = 0
    for i in range(1, len(a) + 1):
        cur = [0] * (len(b) + 1)
        for j in range(1, len(b) + 1):
            if a[i - 1] == b[j - 1]:
                cur[j] = prev[j - 1] + 1
                if cur[j] > best:
                    best = cur[j]
        prev = cur
    return best

lines = open(MD, encoding="utf-8").read().split("\n")
bodies, cur, num = {}, [], None
for l in lines:
    m = re.match(r"^## Q(\d+)\.", l)
    if m:
        if num:
            bodies[num] = "\n".join(cur)
        num, cur = int(m.group(1)), []
    elif num:
        cur.append(l)
if num:
    bodies[num] = "\n".join(cur)

d = json.load(open(SEED, encoding="utf-8"))
flagged = []
for q in d["questions"]:
    body = words(bodies.get(q["q_number"], ""))
    for b in q["blocks"]:
        rs = {}
        for o in b["options"]:
            w = words(o["text"])
            rs[o["label"]] = (lcs_run(w, body) / max(1, len(w)), o["correct"])
        cl = [k for k, v in rs.items() if v[1]][0]
        rc = rs[cl][0]
        rw = max(v[0] for k, v in rs.items() if not v[1])
        if rc >= THR and rc > rw:
            flagged.append((q["q_number"], cl, rc, rw))

for n, l, rc, rw in flagged:
    print(f"Q{n} correct={l} дословность={rc:.2f} (лучший дистрактор {rw:.2f})")
print(f"--- блоков с дословным correct: {len(flagged)} из {sum(len(q['blocks']) for q in d['questions'])} (порог {THR})")
