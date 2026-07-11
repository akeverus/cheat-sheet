#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""MCQ Answer-Parity & Factual-Integrity GATE (pass/fail, read-only).

В отличие от `audit-mcq-parity.py` (мягкий поиск кандидатов, severity-first) этот скрипт —
ЖЁСТКИЙ гейт с именованными проверками и явными порогами из спецификации пользователя
(2026-07-11). Он печатает PASS/FAIL по каждой проверке для каждого файла и выходит с
ненулевым кодом, если хоть одна проверка провалена. Используется в цикле «править, пока не
зелёно» и как pre-save-гейт в mcq-quality-fixer.

Детерминированные проверки (§7 спецификации)
--------------------------------------------
  CORRECT_LONGEST_RATE       доля вопросов, где correct — самый длинный вариант (слов).   fail > 0.40
  OPTION_LENGTH_RATIO        по каждому блоку max/min слов.                                fail любой блок > 1.35
  CORRECT_WRONG_AVG_RATIO    средняя длина correct / средняя длина incorrect (по файлу).  fail вне [0.90, 1.10]
  SENTENCE_PARITY            разница числа предложений между вариантами в блоке.           fail любой блок > 1
  DETAIL_PARITY              доля блоков, где correct — лидер по деталям (code/числа/скобки). fail > 0.40
  ABSOLUTE_MARKER_GAP        доля wrong с абсолютами минус доля correct.                    fail gap > 0.25
  CORRECT_POSITION_DISTRIBUTION  баланс A/B/C/D.                                fail max/min > 2.0
  CORRECT_POSITION_SEQUENCE      длиннейший арифметический прогон позиций correct. fail run > 5
  DUPLICATE_NGRAMS           одинаковые 10-граммы в text разных опций (копипаста).          fail > 3
  TEMPORAL_CLAIMS            годы/версии/«current/latest/deprecated/по умолчанию 20NN».     инфо + завязка на SOURCE_COVERAGE
  SOURCE_COVERAGE            version-sensitive файл обязан иметь fact-freshness sidecar.    fail если нет
  STYLE_GUESSABILITY         детерминированный proxy для ANSWER_WITHOUT_KNOWLEDGE:          fail любая эвристика > 0.40
                             точность угадывания correct по «самый длинный» / «больше code-spans».

LLM-проверки (§7: MULTIPLE_PLAUSIBLE_ANSWERS, ANSWER_WITHOUT_KNOWLEDGE) детерминированным
скриптом не выполняются. Флаг `--blind-export <path>` выгружает JSONL со скрытым correct и
перемешанными вариантами — его читает reviewer-субагент/LLM. STYLE_GUESSABILITY даёт
детерминированный нижний бар для ANSWER_WITHOUT_KNOWLEDGE.

Fact-freshness sidecar
----------------------
`docs/mcq-quality/fact-freshness/<topic_slug>.json`:
  {"topic_slug": "...", "version_sensitive": true, "product_version": "...",
   "checked_date": "YYYY-MM-DD", "sources": ["https://..."],
   "version_sensitive_claims": ["..."]}
Файл считается version-sensitive, если в text есть TEMPORAL_CLAIMS ИЛИ topic в KNOWN_VOLATILE.

Использование
-------------
  python3 scripts/mcq-answer-parity-gate.py <files/dirs...>          # человекочитаемо, PASS/FAIL
  python3 scripts/mcq-answer-parity-gate.py --json <files...>        # машинный JSON
  python3 scripts/mcq-answer-parity-gate.py --blind-export b.jsonl <files...>
  python3 scripts/mcq-answer-parity-gate.py --selftest
Exit 0 — все файлы PASS; exit 1 — есть FAIL; exit 2 — ошибка использования.
"""
from __future__ import annotations
import argparse
import glob
import json
import os
import re
import sys
from collections import Counter

# ── пороги (держать синхронно с mcq-quality-fixer SKILL) ─────────────────────
CORRECT_LONGEST_RATE_MAX = 0.40
OPTION_LENGTH_RATIO_MAX = 1.35
CORRECT_WRONG_AVG_LO = 0.90
CORRECT_WRONG_AVG_HI = 1.10
SENTENCE_PARITY_MAX = 1          # max-min предложений в блоке
DETAIL_LEADER_RATE_MAX = 0.40
ABSOLUTE_MARKER_GAP_MAX = 0.25
POSITION_MAXMIN_MAX = 2.0
POSITION_SEQ_RUN_MAX = 5
DUPLICATE_NGRAM_N = 10
DUPLICATE_NGRAM_MAX = 3
STYLE_GUESS_MAX = 0.40
DETAIL_DOMINATE_DELTA = 2        # correct лидирует по оси, если превышает max-wrong на столько

FACT_FRESHNESS_DIR = "docs/mcq-quality/fact-freshness"
KNOWN_VOLATILE = {
    # темы, где факты меняются с версиями/политиками — требуют sidecar даже без явных дат
    "linkerd-interview", "resilience4j-interview", "api-versioning-interview",
    "hibernate-relationships-interview",
}

BACKTICK_SPAN = re.compile(r"`[^`]*`")
NUMBER_RE = re.compile(r"\d+(?:[.,]\d+)?")
# годы, semver-подобные версии, «латест/деприкейтед/по умолчанию 20NN» и русские временные маркеры
TEMPORAL_RE = re.compile(
    r"\b(?:19|20)\d{2}\b"
    r"|\bv?\d+\.\d+(?:\.\d+)?\b"
    r"|(?:current|latest|deprecated|nowadays)"
    r"|по\s+умолчанию\s+(?:19|20)\d{2}"
    r"|(?:сейчас|сегодня|в\s+настоящее\s+время|на\s+данный\s+момент|актуальн\w*|устарел\w*|по-прежнему)",
    re.IGNORECASE,
)
ABSOLUTE_RE = re.compile(
    r"(?:всегда|никогда|только|исключительно|люб\w*|навсегда|во\s+всех\s+случаях"
    r"|единственн\w*|полностью|гарантированн\w*|обязательно|ни\s+в\s+коем\s+случае"
    r"|не\s+имеет\s+никаких\s+недостатков|нужно\s+делать\s+во\s+всех\s+случаях)",
    re.IGNORECASE,
)
# аббревиатуры, чтобы точка в них не считалась концом предложения
ABBREV = {"т.д", "т.е", "т.к", "т.п", "др", "напр", "рис", "см", "vs", "etc", "e.g", "i.e"}


def strip_code(t: str) -> str:
    return BACKTICK_SPAN.sub(" ", t or "")


def words(t: str) -> int:
    """Число слов; code-span считается одним словом (снимаем его на пробел)."""
    return len([w for w in strip_code(t).split() if any(ch.isalnum() for ch in w)])


def sentences(t: str) -> int:
    """Оценка числа предложений: терминаторы .!?… с фильтром аббревиатур; минимум 1."""
    if not t or not t.strip():
        return 0
    plain = strip_code(t)
    parts = re.split(r"[.!?…]+(?:\s|$)", plain)
    cnt = 0
    for p in parts:
        p = p.strip()
        if not p:
            continue
        last = p.split()[-1].strip(".,:;()").lower() if p.split() else ""
        if last in ABBREV:
            continue
        cnt += 1
    return max(1, cnt)


def code_spans(t: str) -> int:
    return len(BACKTICK_SPAN.findall(t or ""))


def numbers(t: str) -> int:
    return len(NUMBER_RE.findall(strip_code(t)))


def parens(t: str) -> int:
    return (t or "").count("(")


def has_absolute(t: str) -> bool:
    return bool(ABSOLUTE_RE.search(strip_code(t)))


def temporal_hits(t: str):
    return TEMPORAL_RE.findall(t or "")


def tokens_norm(t: str):
    """Токены для n-грамм: нижний регистр, только буквенно-цифровые, code снят."""
    return re.findall(r"[0-9a-zа-яё]+", strip_code(t).lower())


# ── извлечение блоков ────────────────────────────────────────────────────────
def iter_blocks(data):
    for q in data.get("questions", []):
        qn = q.get("q_number")
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            if opts:
                yield qn, b.get("block_idx", 0), opts


def opt_feats(o):
    t = o.get("text", "")
    return {
        "label": o.get("label"),
        "correct": bool(o.get("correct")),
        "words": words(t),
        "sentences": sentences(t),
        "code": code_spans(t),
        "numbers": numbers(t),
        "parens": parens(t),
        "absolute": has_absolute(t),
        "text": t,
    }


# ── проверки уровня файла ────────────────────────────────────────────────────
def analyze(data, topic_slug, has_sidecar, sidecar_ok):
    blocks = []
    for qn, bidx, opts in iter_blocks(data):
        feats = [opt_feats(o) for o in opts]
        blocks.append({"q": qn, "bidx": bidx, "feats": feats})

    nblocks = len(blocks)
    checks = {}

    def add(name, passed, value, detail=""):
        checks[name] = {"pass": bool(passed), "value": value, "detail": detail}

    if nblocks == 0:
        add("EMPTY", False, 0, "нет блоков")
        return checks, {"blocks": 0}

    # CORRECT_LONGEST_RATE — correct строго длиннее ВСЕХ дистракторов (tie ≠ угадываемо)
    longest = 0
    for b in blocks:
        cor = next((f for f in b["feats"] if f["correct"]), None)
        wrongs = [f for f in b["feats"] if not f["correct"]]
        if cor is None or not wrongs:
            continue
        if cor["words"] > max(w["words"] for w in wrongs):
            longest += 1
    clr = longest / nblocks
    add("CORRECT_LONGEST_RATE", clr <= CORRECT_LONGEST_RATE_MAX, round(clr, 3),
        f"{longest}/{nblocks} блоков correct-самый-длинный (порог ≤{CORRECT_LONGEST_RATE_MAX})")

    # OPTION_LENGTH_RATIO (per-block max/min words)
    worst_ratio, worst_q, viol = 0.0, None, 0
    for b in blocks:
        ws = [max(1, f["words"]) for f in b["feats"]]
        r = max(ws) / min(ws)
        if r > worst_ratio:
            worst_ratio, worst_q = r, b["q"]
        if r > OPTION_LENGTH_RATIO_MAX:
            viol += 1
    add("OPTION_LENGTH_RATIO", viol == 0, round(worst_ratio, 2),
        f"худший блок Q{worst_q}={worst_ratio:.2f}; {viol} блоков > {OPTION_LENGTH_RATIO_MAX}")

    # CORRECT_WRONG_AVG_RATIO (file-level mean)
    cw, cn, ww, wn = 0, 0, 0, 0
    for b in blocks:
        for f in b["feats"]:
            if f["correct"]:
                cw += f["words"]; cn += 1
            else:
                ww += f["words"]; wn += 1
    mean_c = cw / cn if cn else 0
    mean_w = ww / wn if wn else 0
    ratio = (mean_c / mean_w) if mean_w else 0
    add("CORRECT_WRONG_AVG_RATIO", CORRECT_WRONG_AVG_LO <= ratio <= CORRECT_WRONG_AVG_HI,
        round(ratio, 3), f"correct̄={mean_c:.0f}сл / wronḡ={mean_w:.0f}сл (диапазон {CORRECT_WRONG_AVG_LO}–{CORRECT_WRONG_AVG_HI})")

    # SENTENCE_PARITY
    sviol, sworst, sworstq = 0, 0, None
    for b in blocks:
        ss = [f["sentences"] for f in b["feats"]]
        gap = max(ss) - min(ss)
        if gap > sworst:
            sworst, sworstq = gap, b["q"]
        if gap > SENTENCE_PARITY_MAX:
            sviol += 1
    add("SENTENCE_PARITY", sviol == 0, sworst,
        f"худший разрыв Q{sworstq}={sworst} предл.; {sviol} блоков > {SENTENCE_PARITY_MAX}")

    # DETAIL_PARITY (correct — лидер по code/numbers/parens)
    leader = 0
    for b in blocks:
        cor = next((f for f in b["feats"] if f["correct"]), None)
        wrongs = [f for f in b["feats"] if not f["correct"]]
        if not cor or not wrongs:
            continue
        dom = False
        for ax in ("code", "numbers", "parens"):
            if cor[ax] >= max(w[ax] for w in wrongs) + DETAIL_DOMINATE_DELTA:
                dom = True
        if dom:
            leader += 1
    dlr = leader / nblocks
    add("DETAIL_PARITY", dlr <= DETAIL_LEADER_RATE_MAX, round(dlr, 3),
        f"{leader}/{nblocks} блоков correct доминирует по деталям (порог ≤{DETAIL_LEADER_RATE_MAX})")

    # ABSOLUTE_MARKER_GAP
    cabs = sum(1 for b in blocks for f in b["feats"] if f["correct"] and f["absolute"])
    wabs = sum(1 for b in blocks for f in b["feats"] if not f["correct"] and f["absolute"])
    crate = cabs / cn if cn else 0
    wrate = wabs / wn if wn else 0
    gap = wrate - crate
    add("ABSOLUTE_MARKER_GAP", gap <= ABSOLUTE_MARKER_GAP_MAX, round(gap, 3),
        f"wrong={wrate:.2f} vs correct={crate:.2f} (разрыв ≤{ABSOLUTE_MARKER_GAP_MAX})")

    # CORRECT_POSITION_DISTRIBUTION
    pos = Counter()
    for b in blocks:
        for f in b["feats"]:
            if f["correct"]:
                pos[f["label"]] += 1
    counts = [pos.get(l, 0) for l in ("A", "B", "C", "D")]
    mx, mn = max(counts), min(counts)
    mm = (mx / mn) if mn else float("inf")
    add("CORRECT_POSITION_DISTRIBUTION", mm <= POSITION_MAXMIN_MAX,
        (round(mm, 2) if mn else "inf"),
        f"A/B/C/D={counts} max/min={'inf' if not mn else round(mm,2)} (порог ≤{POSITION_MAXMIN_MAX})")

    # CORRECT_POSITION_SEQUENCE — предсказуемость последовательности позиций correct.
    # Баланс A/B/C/D может быть идеальным, но при строгом арифметическом цикле
    # (A,B,C,D,A,B,C,D…) позицию можно угадать, зная предыдущие. Ловим самый длинный
    # прогон, где соседние correct-метки идут с постоянным шагом по модулю 4.
    LAB = "ABCD"
    seq = [f["label"] for b in blocks for f in b["feats"] if f["correct"]]
    idx = [LAB.index(x) for x in seq if x in LAB]
    maxrun = 1 if idx else 0
    for delta in range(4):
        run = 1
        for i in range(1, len(idx)):
            if (idx[i] - idx[i - 1]) % 4 == delta:
                run += 1
                maxrun = max(maxrun, run)
            else:
                run = 1
    add("CORRECT_POSITION_SEQUENCE", maxrun <= POSITION_SEQ_RUN_MAX, maxrun,
        f"самый длинный арифметический прогон позиций = {maxrun} (порог ≤{POSITION_SEQ_RUN_MAX}); "
        f"строгий A→B→C→D цикл угадывается")

    # DUPLICATE_NGRAMS (across different option texts in the file)
    seen = {}
    dups = Counter()
    dup_examples = []
    for b in blocks:
        for f in b["feats"]:
            toks = tokens_norm(f["text"])
            key = (b["q"], f["label"])
            for i in range(len(toks) - DUPLICATE_NGRAM_N + 1):
                shingle = " ".join(toks[i:i + DUPLICATE_NGRAM_N])
                if shingle in seen and seen[shingle] != key:
                    if dups[shingle] == 0:
                        dup_examples.append((seen[shingle], key, shingle))
                    dups[shingle] += 1
                else:
                    seen.setdefault(shingle, key)
    ndup = len(dups)
    add("DUPLICATE_NGRAMS", ndup <= DUPLICATE_NGRAM_MAX, ndup,
        f"{ndup} повторяющихся {DUPLICATE_NGRAM_N}-грамм между разными опциями (порог ≤{DUPLICATE_NGRAM_MAX})"
        + (f"; напр. «…{dup_examples[0][2][:48]}…»" if dup_examples else ""))

    # TEMPORAL_CLAIMS (инфо; завязка на SOURCE_COVERAGE)
    temporal = []
    for b in blocks:
        for f in b["feats"]:
            hits = temporal_hits(f["text"])
            if hits:
                temporal.append((b["q"], f["label"], hits[:5]))
    file_is_volatile = bool(temporal) or (topic_slug in KNOWN_VOLATILE)
    add("TEMPORAL_CLAIMS", True, len(temporal),
        f"{len(temporal)} опций с временными/версионными утверждениями"
        + ("" if not temporal else f" (напр. Q{temporal[0][0]}{temporal[0][1]}: {temporal[0][2]})"))

    # SOURCE_COVERAGE
    if file_is_volatile:
        add("SOURCE_COVERAGE", has_sidecar and sidecar_ok,
            "ok" if (has_sidecar and sidecar_ok) else "missing",
            f"version-sensitive → нужен {FACT_FRESHNESS_DIR}/{topic_slug}.json с product_version+checked_date+sources"
            + ("" if has_sidecar else " (ОТСУТСТВУЕТ)")
            + ("" if (not has_sidecar or sidecar_ok) else " (НЕПОЛНЫЙ)"))
    else:
        add("SOURCE_COVERAGE", True, "n/a", "не version-sensitive")

    # STYLE_GUESSABILITY — детерминированный proxy для ANSWER_WITHOUT_KNOWLEDGE
    def guess_rate(axis):
        hit = 0
        for b in blocks:
            cor = next((f for f in b["feats"] if f["correct"]), None)
            if cor is None:
                continue
            mx = max(f[axis] for f in b["feats"])
            winners = [f for f in b["feats"] if f[axis] == mx]
            # доля вероятности угадать correct, выбирая среди лидеров оси
            if cor[axis] == mx:
                hit += 1.0 / len(winners)
        return hit / nblocks
    g_len = guess_rate("words")
    g_code = guess_rate("code")
    worst = max(g_len, g_code)
    add("STYLE_GUESSABILITY", worst <= STYLE_GUESS_MAX, round(worst, 3),
        f"угадывание по длине={g_len:.2f}, по code-spans={g_code:.2f} (шанс=0.25, порог ≤{STYLE_GUESS_MAX})")

    meta = {"blocks": nblocks, "volatile": file_is_volatile}
    return checks, meta


# ── sidecar ──────────────────────────────────────────────────────────────────
def load_sidecar(topic_slug, repo_root):
    path = os.path.join(repo_root, FACT_FRESHNESS_DIR, f"{topic_slug}.json")
    if not os.path.exists(path):
        return False, False, None
    try:
        sc = json.load(open(path, encoding="utf-8"))
    except Exception as e:
        return True, False, f"невалидный JSON: {e}"
    required = ["product_version", "checked_date", "sources"]
    ok = all(sc.get(k) for k in required) and isinstance(sc.get("sources"), list) and len(sc["sources"]) >= 1
    return True, ok, sc


def repo_root_of(path):
    p = os.path.abspath(path)
    while p != "/":
        if os.path.isdir(os.path.join(p, ".git")):
            return p
        p = os.path.dirname(p)
    return os.getcwd()


# ── driver ───────────────────────────────────────────────────────────────────
def collect(paths):
    out = []
    for p in paths:
        if os.path.isdir(p):
            out += sorted(glob.glob(os.path.join(p, "**", "*.json"), recursive=True))
        elif p.endswith(".json"):
            out.append(p)
    return out


def run_file(path):
    data = json.load(open(path, encoding="utf-8"))
    if "questions" not in data:
        return None
    topic = data.get("topic_slug") or os.path.basename(path).replace(".json", "")
    root = repo_root_of(path)
    has_sidecar, sidecar_ok, _ = load_sidecar(topic, root)
    checks, meta = analyze(data, topic, has_sidecar, sidecar_ok)
    passed = all(c["pass"] for c in checks.values())
    return {"path": path, "topic": topic, "pass": passed, "checks": checks, "meta": meta}


def blind_export(paths, out_path):
    """JSONL для LLM blind-style / multiple-plausible проверок: correct скрыт, порядок как есть."""
    n = 0
    with open(out_path, "w", encoding="utf-8") as fh:
        for path in collect(paths):
            try:
                data = json.load(open(path, encoding="utf-8"))
            except Exception:
                continue
            if "questions" not in data:
                continue
            for qn, bidx, opts in iter_blocks(data):
                rec = {
                    "file": os.path.basename(path),
                    "topic": data.get("topic_slug"),
                    "q_number": qn, "block_idx": bidx,
                    "question_text": opts and data,  # placeholder replaced below
                }
                # найти question_text
                qt = None
                for q in data["questions"]:
                    if q.get("q_number") == qn:
                        for b in q["blocks"]:
                            if b.get("block_idx", 0) == bidx:
                                qt = b.get("question_text")
                rec["question_text"] = qt
                rec["options"] = [{"label": o.get("label"), "text": o.get("text", "")} for o in opts]
                rec["correct_label"] = next((o.get("label") for o in opts if o.get("correct")), None)
                fh.write(json.dumps(rec, ensure_ascii=False) + "\n")
                n += 1
    return n


def render_human(results):
    allpass = True
    for r in results:
        allpass = allpass and r["pass"]
        head = "PASS" if r["pass"] else "FAIL"
        print(f"\n{'✅' if r['pass'] else '❌'} {head}  {r['topic']}  ({r['meta']['blocks']} блоков)")
        for name, c in r["checks"].items():
            mark = "  ok " if c["pass"] else " FAIL"
            print(f"   [{mark}] {name:28s} = {str(c['value']):>7}  — {c['detail']}")
    print("\n" + "─" * 72)
    npass = sum(1 for r in results if r["pass"])
    print(f"Итог: {npass}/{len(results)} файлов PASS.")
    return allpass


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("paths", nargs="*", help="файлы/каталоги seed/mcq")
    ap.add_argument("--json", action="store_true", help="машинный JSON вместо человекочитаемого")
    ap.add_argument("--blind-export", metavar="PATH", help="выгрузить JSONL для LLM blind-checks и выйти")
    ap.add_argument("--selftest", action="store_true", help="прогнать встроенные фикстуры")
    args = ap.parse_args()

    if args.selftest:
        return selftest()

    if args.blind_export:
        if not args.paths:
            print("нужны пути", file=sys.stderr); return 2
        n = blind_export(args.paths, args.blind_export)
        print(f"blind-export: {n} блоков → {args.blind_export}")
        return 0

    if not args.paths:
        ap.print_help(); return 2

    results = []
    for path in collect(args.paths):
        try:
            r = run_file(path)
        except Exception as e:
            print(f"⚠ {path}: {e}", file=sys.stderr); continue
        if r:
            results.append(r)

    if args.json:
        print(json.dumps(results, ensure_ascii=False, indent=2))
        return 0 if all(r["pass"] for r in results) else 1

    allpass = render_human(results)
    return 0 if allpass else 1


def selftest():
    # плохой блок: correct длинный/детальный, wrong — заглушки с абсолютами
    bad = {
        "topic_slug": "x-interview",
        "questions": [{
            "q_number": 1,
            "blocks": [{
                "block_idx": 0,
                "question_text": "Что верно про `LAZY`?",
                "options": [
                    {"order": 0, "label": "A", "correct": True,
                     "text": "`LAZY` откладывает загрузку связи до первого обращения через `getX()`, а `EAGER` грузит сразу с parent (обычно 2 запроса, иногда 1 JOIN); дефолты: `*ToMany` — `LAZY`, `*ToOne` — `EAGER`."},
                    {"order": 1, "label": "B", "correct": False, "text": "`LAZY` всегда быстрее."},
                    {"order": 2, "label": "C", "correct": False, "text": "`EAGER` только для списков."},
                    {"order": 3, "label": "D", "correct": False, "text": "Это одно и то же."},
                ],
            }],
        }],
    }
    checks, meta = analyze(bad, "x-interview", False, False)
    assert not checks["CORRECT_LONGEST_RATE"]["pass"], checks["CORRECT_LONGEST_RATE"]
    assert not checks["OPTION_LENGTH_RATIO"]["pass"], checks["OPTION_LENGTH_RATIO"]
    assert not checks["STYLE_GUESSABILITY"]["pass"], checks["STYLE_GUESSABILITY"]
    assert not checks["ABSOLUTE_MARKER_GAP"]["pass"], checks["ABSOLUTE_MARKER_GAP"]
    # хороший блок: строгий паритет длины/структуры/деталей, у каждого своя ошибка
    good = {
        "topic_slug": "y-interview",
        "questions": [{
            "q_number": 1,
            "blocks": [{
                "block_idx": 0,
                "question_text": "Чем отличаются `LAZY` и `EAGER`?",
                "options": [
                    {"order": 0, "label": "A", "correct": False,
                     "text": "`LAZY` меняет момент загрузки до первого обращения, а `EAGER` грузит сразу; по дефолту он `*ToMany`."},
                    {"order": 1, "label": "B", "correct": True,
                     "text": "`LAZY` откладывает связь загрузки до первого обращения, а `EAGER` грузит сразу; по дефолту он `*ToOne`."},
                    {"order": 2, "label": "C", "correct": False,
                     "text": "`EAGER` кэширует связь загрузки до первого обращения, а `LAZY` грузит сразу; по дефолту он `entity`."},
                    {"order": 3, "label": "D", "correct": False,
                     "text": "`FETCH` включает связь загрузки до первого обращения, а `JOIN` грузит сразу; по дефолту он `graph`."},
                ],
            }],
        }],
    }
    checks2, _ = analyze(good, "y-interview", False, False)
    assert checks2["OPTION_LENGTH_RATIO"]["pass"], checks2["OPTION_LENGTH_RATIO"]
    assert checks2["CORRECT_LONGEST_RATE"]["pass"], checks2["CORRECT_LONGEST_RATE"]
    print("selftest OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
