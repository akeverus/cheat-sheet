#!/usr/bin/env python3
"""Сборка seed JSON из авторских блоков: question_text = заголовок, ABCD-баланс, cap-1-backtick.
  build.py <authored.json> <qt.json> <out_seed.json> <slug> <seed_int>"""
import json, re, random, html, sys
from collections import Counter

AUTH, QT, OUT, SLUG, SEED = sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], int(sys.argv[5])
qt = {int(k): v for k, v in json.load(open(QT, encoding="utf-8")).items()}
authored = json.load(open(AUTH, encoding="utf-8"))["blocks"]

def unesc(o):
    if isinstance(o, str):
        prev, cur = None, o
        while cur != prev: prev, cur = cur, html.unescape(cur)
        return cur
    if isinstance(o, list): return [unesc(x) for x in o]
    if isinstance(o, dict): return {k: unesc(v) for k, v in o.items()}
    return o

authored = unesc(authored)
by_q = {b["q_number"]: b for b in authored}
LABELS = ["A", "B", "C", "D"]

def cap_one_span(t):
    spans = list(re.finditer(r"`[^`]+`", t))
    if len(spans) <= 1: return t
    out, last = [], 0
    for i, m in enumerate(spans):
        out.append(t[last:m.start()])
        out.append(m.group(0) if i == 0 else m.group(0)[1:-1])
        last = m.end()
    out.append(t[last:]); return "".join(out)

norm = lambda t: re.sub(r"\s+", " ", cap_one_span(t.strip()))

def targets(qnums):
    n = len(qnums)
    base = [0, 1, 2, 3] * (n // 4) + list(range(n % 4))
    rng = random.Random(SEED)
    for _ in range(400):
        seq = base[:]; rng.shuffle(seq)
        if any(seq[i] == seq[i-1] == seq[i-2] for i in range(2, n)): continue
        maxrun, cur = 1, 1
        for i in range(1, n):
            d = (seq[i] - seq[i-1]) % 4
            dp = (seq[i-1] - seq[i-2]) % 4 if i >= 2 else None
            if dp is not None and d == dp: cur += 1; maxrun = max(maxrun, cur)
            else: cur = 2
        if maxrun <= 3: return dict(zip(qnums, seq))
    return dict(zip(qnums, base))

qnums = sorted(by_q)
missing = [n for n in sorted(qt) if n not in by_q]
if missing: sys.exit(f"ОТСУТСТВУЮТ блоки: {missing}")
tg = targets(qnums)
questions = []
for q in qnums:
    b = by_q[q]
    correct = {"text": norm(b["correct_text"]), "correct": True, "sections": b["correct_sections"]}
    wrongs = [{"text": norm(w["text"]), "correct": False, "sections": {
        k: w[k] for k in ("what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be")}}
        for w in b["wrong"]]
    # финальная пунктуация одинакова у всех четырёх: иначе «единственный без точки»
    # выдаёт правильный ответ глазом, а счётчики слов и символов этого не видят
    for o in [correct] + wrongs:
        t = o["text"].rstrip()
        o["text"] = t if t.endswith((".", "!", "?")) else t + "."
    slots = [None] * 4; slots[tg[q]] = correct; wi = 0
    for i in range(4):
        if slots[i] is None: slots[i] = wrongs[wi]; wi += 1
    questions.append({"q_number": q, "blocks": [{"block_idx": 0, "question_text": qt[q],
        "options": [{"order": i, "label": LABELS[i], "text": o["text"], "correct": o["correct"],
                     "sections": o["sections"]} for i, o in enumerate(slots)]}]})

json.dump({"topic_slug": SLUG, "questions": questions}, open(OUT, "w", encoding="utf-8"),
          ensure_ascii=False, indent=2); open(OUT, "a").write("\n")
blob = open(OUT, encoding="utf-8").read()
ent = re.findall(r"&(?:lt|gt|amp|quot);", blob)
assert not ent, f"HTML-сущности в сиде: {Counter(ent)}"
dist = Counter(o["label"] for q in questions for o in q["blocks"][0]["options"] if o["correct"])
print(f"собрано: {len(questions)} вопросов, {len(questions)*4} вариантов")
print("ABCD correct:", dict(sorted(dist.items())), "| html-ent:", len(ent))
