#!/usr/bin/env python3
"""Выгрузка блоков к правке для ремонтного Workflow + применение результата.
  struct_payload.py dump  <seed.json> <out.json> [source.md] — нарушенные блоки
  struct_payload.py apply <seed.json> <journal...>           — вписать новые тексты

С третьим аргументом добавляет к нарушениям дословность правильного ответа
относительно тела своего Q (общий непрерывный отрезок слов), чтобы один прогон
чинил и форму, и лексическое эхо."""
import json, re, sys

cl = lambda t: 1 + t.count(",") + t.count("—") + t.count(";")
SP = lambda t: len(re.findall(r"`[^`]+`", t))
W = lambda t: len(re.findall(r"\w+", re.sub(r"`[^`]*`", "", t), flags=re.U))


ABS = re.compile(r"\b(всегда|никогда|только|единственн\w*|люб(?:ой|ое|ая|ых|ым)|"
                 r"вес|весь|вся|всё|все|всю|всей|всех|всем|целиком|обязательн\w*|"
                 r"нельзя|невозможно)\b", re.I)
HEDGE = re.compile(r"\b(обычно|как правило|зачастую|может быть|возможно|скорее всего|"
                   r"иногда|в целом)\b", re.I)
NEG = re.compile(r"\b(не|нет|ни|без)\b", re.I)
CONTRAST = re.compile(r"\b(а|но|тогда как|зато|в отличие|вместо)\b", re.I)
UNIQ = {
    "длина":         len,
    "двоеточие":     lambda t: t.count(":"),
    "точка с запятой": lambda t: t.count(";"),
    "цифра":         lambda t: len(re.findall(r"\d", t)),
    "тире":          lambda t: t.count("—"),
    "отрицание":     lambda t: len(NEG.findall(t)),
    "абсолютный оборот": lambda t: len(ABS.findall(t)),
    "смягчение":     lambda t: len(HEDGE.findall(t)),
    "противопоставление": lambda t: len(CONTRAST.findall(t)),
}


def unique_marks(opts):
    """Признаки, по которым правильный ответ уникально выделяется среди четырёх."""
    c = [o for o in opts if o["correct"]][0]
    w = [o for o in opts if not o["correct"]]
    out = []
    for name, f in UNIQ.items():
        vc, vw = f(c["text"]), [f(x["text"]) for x in w]
        if vc > max(vw):
            out.append(f"правильный — единственный, у кого есть {name} ({vc} против {vw})")
        elif vc < min(vw):
            out.append(f"правильный — единственный, у кого нет {name} ({vc} против {vw})")
    return out


# parity-гейт режет текст по пробелам: «по-прежнему» для него ОДНО слово,
# а W() ниже считает его двумя. Разброс max/min по ЭТОМУ счёту не должен
# превышать 1.35, иначе гейт валит файл, а коридор по W() при этом чист.
GW = lambda t: len([w for w in re.sub(r"`[^`]*`", "", t).split()
                    if any(ch.isalnum() for ch in w)])


def violations(opts):
    c = [o for o in opts if o["correct"]][0]
    L, P, S, Wc = len(c["text"]), cl(c["text"]), SP(c["text"]), W(c["text"])
    bad = []
    for x in opts:
        if x["correct"]:
            continue
        r, rw = len(x["text"]) / max(1, L), W(x["text"]) / max(1, Wc)
        if not 0.85 <= r <= 1.15:
            bad.append(f"{x['label']}: длина {len(x['text'])} против {L} у правильного ({r:.2f}x)")
        if cl(x["text"]) != P:
            bad.append(f"{x['label']}: частей фразы {cl(x['text'])} против {P}")
        if abs(SP(x["text"]) - S) > 1:
            bad.append(f"{x['label']}: код-спанов {SP(x['text'])} против {S}")
        if not 0.77 <= rw <= 1.30:
            bad.append(f"{x['label']}: слов {W(x['text'])} против {Wc} ({rw:.2f}x)")
    gws = [GW(o["text"]) for o in opts]
    if max(gws) / max(1, min(gws)) > 1.35:
        bad.append("разброс длины по счёту через пробелы "
                   f"{max(gws)}/{min(gws)} = {max(gws)/max(1,min(gws)):.2f} — выше 1.35; "
                   "выровнять, НЕ добирая дефисными словами")
    return bad


def wtok(t):
    t = re.sub(r"`([^`]*)`", r"\1", t.lower())
    return re.findall(r"[a-zа-яё0-9@._-]+", t)


def lcs_run(a, b):
    if not a or not b:
        return 0, ""
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
    return best, " ".join(a[end - best:end])


def read_bodies(md):
    bodies, cur, num = {}, [], None
    for l in open(md, encoding="utf-8").read().split("\n"):
        m = re.match(r"^## Q(\d+)\.", l)
        if m:
            if num:
                bodies[num] = "\n".join(cur)
            num, cur = int(m.group(1)), []
        elif num:
            cur.append(l)
    if num:
        bodies[num] = "\n".join(cur)
    return bodies


if sys.argv[1] == "dump":
    d = json.load(open(sys.argv[2], encoding="utf-8"))
    bodies = read_bodies(sys.argv[4]) if len(sys.argv) > 4 else {}
    out = []
    for q in d["questions"]:
        for b in q["blocks"]:
            bad = violations(b["options"]) + unique_marks(b["options"])
            if bodies:
                c = [o for o in b["options"] if o["correct"]][0]
                bw = wtok(bodies.get(q["q_number"], ""))
                n, span = lcs_run(wtok(c["text"]), bw)
                rc = n / max(1, len(wtok(c["text"])))
                rw = max((lcs_run(wtok(x["text"]), bw)[0] / max(1, len(wtok(x["text"])))
                          for x in b["options"] if not x["correct"]), default=0)
                if rc >= 0.25 and rc > rw:
                    bad.append(f"правильный дословно повторяет тело ({rc:.2f} против {rw:.2f} "
                               f"у дистрактора), общий отрезок: «{span}» — ПЕРЕСКАЖИ СВОИМИ СЛОВАМИ, "
                               f"утверждение и структуру сохрани")
            if not bad:
                continue
            out.append({"q_number": q["q_number"], "нарушения": bad,
                        "options": [{"label": o["label"], "correct": o["correct"], "text": o["text"]}
                                    for o in sorted(b["options"], key=lambda x: x["order"])]})
    json.dump(out, open(sys.argv[3], "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print(f"блоков к правке: {len(out)} из {sum(len(q['blocks']) for q in d['questions'])}"
          f" -> {sys.argv[3]}")

elif sys.argv[1] == "apply":
    seed = sys.argv[2]
    new = {}
    for jp in sys.argv[3:]:
        for line in open(jp, encoding="utf-8"):
            for m in re.finditer(r'\{"blocks":\s*\[.*?\]\s*\}', line):
                try:
                    for b in json.loads(m.group(0))["blocks"]:
                        new[b["q_number"]] = {o["label"]: o["text"] for o in b["options"]}
                except Exception:
                    pass
            try:
                j = json.loads(line)
            except Exception:
                continue
            r = j.get("result") if isinstance(j, dict) else None
            if isinstance(r, dict) and "blocks" in r:
                for b in r["blocks"]:
                    new[b["q_number"]] = {o["label"]: o["text"] for o in b["options"]}
    d = json.load(open(seed, encoding="utf-8"))
    n = 0
    for q in d["questions"]:
        if q["q_number"] not in new:
            continue
        for o in q["blocks"][0]["options"]:
            t = new[q["q_number"]].get(o["label"])
            if t and t.strip() and t != o["text"]:
                t = t.strip()
                o["text"] = t if t.endswith((".", "!", "?")) else t + "."
                n += 1
    json.dump(d, open(seed, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
    open(seed, "a").write("\n")
    print(f"обновлено вариантов: {n} в {len(new)} блоках")
