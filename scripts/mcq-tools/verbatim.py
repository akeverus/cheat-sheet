#!/usr/bin/env python3
"""Дословность опции относительно тела своего ## Q: длина самого длинного общего
непрерывного отрезка слов, делённая на число слов опции.
  verbatim.py <seed.json> <source.md> [порог]
Печатает блоки, где correct дословнее порога И дословнее лучшего дистрактора."""
import json, re, sys

ARGS = [a for a in sys.argv[1:] if a != "--spans"]
SPANS = "--spans" in sys.argv
SEED, MD = ARGS[0], ARGS[1]
THR = float(ARGS[2]) if len(ARGS) > 2 else 0.25

def words(t):
    t = re.sub(r"`([^`]*)`", r"\1", t.lower())
    return re.findall(r"[a-zа-яё0-9@._-]+", t)

def lcs_run(a, b, want_text=False):
    """Длина самого длинного общего непрерывного отрезка (и он сам при want_text)."""
    if not a or not b:
        return ("", 0) if want_text else 0
    prev = [0] * (len(b) + 1)
    best, end = 0, 0
    for i in range(1, len(a) + 1):
        cur = [0] * (len(b) + 1)
        for j in range(1, len(b) + 1):
            if a[i - 1] == b[j - 1]:
                cur[j] = prev[j - 1] + 1
                if cur[j] > best:
                    best, end = cur[j], i
        prev = cur
    return (" ".join(a[end - best:end]), best) if want_text else best

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

def coverage(a, b, minrun=3):
    """Доля слов опции, попавших хоть в один общий с телом отрезок длиной >= minrun.
    Ловит СКЛЕЙКУ нескольких коротких цитат — её самый длинный отрезок не выдаёт."""
    if not a or not b:
        return 0.0
    marked = [False] * len(a)
    for i in range(len(a)):
        for j in range(len(a), i + minrun - 1, -1):
            seg = a[i:j]
            if len(seg) < minrun:
                break
            if any(b[k:k + len(seg)] == seg for k in range(len(b) - len(seg) + 1)):
                for t in range(i, j):
                    marked[t] = True
                break
    return sum(marked) / len(a)


COV_THR = 0.50
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
        c = [o for o in b["options"] if o["correct"]][0]
        cvc = coverage(words(c["text"]), body)
        cvw = max(coverage(words(x["text"]), body)
                  for x in b["options"] if not x["correct"])
        if (rc >= THR and rc > rw) or (cvc >= COV_THR and cvc > cvw):
            flagged.append((q["q_number"], cl, rc, rw, cvc, cvw))

for n, l, rc, rw, cvc, cvw in flagged:
    line = (f"Q{n} correct={l} отрезок={rc:.2f} (дистрактор {rw:.2f}) "
            f"покрытие={cvc:.2f} (дистрактор {cvw:.2f})")
    if SPANS:
        q = [x for x in d["questions"] if x["q_number"] == n][0]
        o = [x for x in q["blocks"][0]["options"] if x["correct"]][0]
        txt, ln = lcs_run(words(o["text"]), words(bodies.get(n, "")), want_text=True)
        line += f"\n     общий отрезок ({ln} сл.): «{txt}»"
    print(line)
print(f"--- блоков с дословным correct: {len(flagged)} из "
      f"{sum(len(q['blocks']) for q in d['questions'])} "
      f"(порог отрезка {THR}, порог покрытия {COV_THR})")
