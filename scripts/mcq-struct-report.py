#!/usr/bin/env python3
"""Блоки, где структура опций разъехалась, с целевыми числами для точечной доработки.
Паритет считается по ЧЕТЫРЁМ осям сразу: символы, части фразы, код-спаны, значащие слова.
  mcq-struct-report.py <seed.json> [--json out.json]
"""
import json, re, sys

S = sys.argv[1]
cl = lambda t: 1 + t.count(",") + t.count("—") + t.count(";")
SP = lambda t: len(re.findall(r"`[^`]+`", t))
W = lambda t: len(re.findall(r"\w+", re.sub(r"`[^`]*`", "", t), flags=re.U))

d = json.load(open(S, encoding="utf-8"))
bad = []
for q in d["questions"]:
  for _b in q["blocks"]:
    o = _b["options"]
    c = [x for x in o if x["correct"]][0]
    w = [x for x in o if not x["correct"]]
    L, CL, SPc, Wc = len(c["text"]), cl(c["text"]), SP(c["text"]), W(c["text"])
    probs = []
    for x in w:
        r = len(x["text"]) / L
        if not 0.85 <= r <= 1.15: probs.append(f"{x['label']}:симв={len(x['text'])}({r:.2f}x)")
        if cl(x["text"]) != CL:   probs.append(f"{x['label']}:частей={cl(x['text'])}≠{CL}")
        if abs(SP(x["text"]) - SPc) > 1: probs.append(f"{x['label']}:спанов={SP(x['text'])}≠{SPc}")
        rw = W(x["text"]) / max(1, Wc)
        if not 0.77 <= rw <= 1.30: probs.append(f"{x['label']}:слов={W(x['text'])}({rw:.2f}x)")
    if probs:
        bad.append({"q_number": q["q_number"], "target_chars": L, "target_clauses": CL,
                    "target_spans": SPc, "target_words": Wc, "problems": probs,
                    "correct_text": c["text"],
                    "wrong": [{"label": x["label"], "text": x["text"]} for x in w]})
for b in bad:
    print(f"Q{b['q_number']}: цель {b['target_chars']}c/{b['target_clauses']}ч/"
          f"{b['target_spans']}сп/{b['target_words']}сл — " + "; ".join(b["problems"]))
print(f"блоков вне коридора: {len(bad)} из {sum(len(q['blocks']) for q in d['questions'])}")
if "--json" in sys.argv:
    json.dump(bad, open(sys.argv[sys.argv.index("--json") + 1], "w", encoding="utf-8"),
              ensure_ascii=False, indent=1)
