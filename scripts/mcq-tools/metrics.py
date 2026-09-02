"""Общие метрики паритета (импортируется соседями)."""
import re
gw = lambda t: len([w for w in re.sub(r"`[^`]*`", "", t).split() if any(c.isalnum() for c in w)])
W = lambda t: len(re.findall(r"\w+", re.sub(r"`[^`]*`", "", t), flags=re.U))
cl = lambda t: 1 + t.count(",") + t.count("—") + t.count(";")
sp = lambda t: len(re.findall(r"`[^`]+`", t))
def in_corridor(c, t):
    rc, rw, rW = len(t) / len(c), gw(t) / gw(c), W(t) / max(W(c), 1)
    return (0.85 <= rc <= 1.15) and cl(t) == cl(c) and abs(sp(t) - sp(c)) <= 1 and (0.77 <= rw <= 1.30) and (0.77 <= rW <= 1.30)
FEATS = {
    "начало латиницей": lambda t: bool(re.match(r"[`A-Za-z]", t)),
    "двоеточие": lambda t: ":" in t,
    "точка с запятой": lambda t: ";" in t,
    "цифра": lambda t: bool(re.search(r"\d", re.sub(r"`[^`]*`", "", t))),
    "тире": lambda t: "—" in t,
    "отрицание": lambda t: bool(re.search(r"\b(не|нет|без|ни)\b", t, re.I)),
    "абсолютное": lambda t: bool(re.search(r"\b(всегда|никогда|люб\w+|все|всё|только|исключительно|единственн\w*|целиком|полностью)", t, re.I)),
    "смягчение": lambda t: bool(re.search(r"\b(обычно|как правило|чаще|может|можно|иногда|порой)", t, re.I)),
    "противопоставление": lambda t: bool(re.search(r"(, а |, но |, тогда как|вместо )", t, re.I)),
}
def tells(texts):
    out = []
    for k, f in FEATS.items():
        v = [f(x) for x in texts]
        if v[0] and not any(v[1:]): out.append(f"уникально у correct: {k}")
        if (not v[0]) and all(v[1:]): out.append(f"уникально ОТСУТСТВУЕТ у correct: {k}")
    return out
