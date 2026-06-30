#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Аудит «угадываемости» правильного варианта в MCQ JSON-сидерах (read-only).

Находит блоки, где correct можно угадать БЕЗ знания темы — по длине, структуре,
технической плотности, или где дистракторы выглядят как короткие заглушки / раздутые
карикатуры. Скрипт НЕ переписывает контент — только находит кандидатов на ручную правку
и объясняет, почему блок подозрительный. Правило-первоисточник — `mcq-quality-fixer`
(разделы «Option Parity», «Plausibility Parity», анти-паттерн «Inflated Caricature Distractor»).

Сигналы на уровне блока
-----------------------
Длина:
  LEN_AVG              len(correct) / avg(len(wrong)) > 1.3
  LEN_SPREAD           max(len опций) / min(len опций) > 1.5
  WORD_COUNT_GAP       слов в correct заметно больше, чем в любом wrong
  SENTENCE_COUNT_GAP   предложений в correct заметно больше
Структура:
  UNIQ_MARKER          correct единственный несёт →/«: a; b»/;/≥2 backtick/шаг-список
  STRUCTURE_DENSITY_GAP суммарная «структурность» (→/;/:) correct >> wrong
  ONLY_ONE_SEQUENCE_OPTION   sequence-форму (→) несёт ровно один вариант
  ONLY_ONE_ALGORITHM_OPTION  шаг/алгоритм-форму несёт ровно один вариант
  ONLY_ONE_ENUMERATION_OPTION colon/semicolon-перечисление несёт ровно один вариант
  ONLY_ONE_CAUSAL_OPTION     причинно-следственную связку несёт ровно один вариант
Тех. плотность:
  TECH_DENSITY_GAP     композитная тех-плотность correct >> wrong
  BACKTICK_GAP         backtick-терминов в correct заметно больше
  NUMBER_GAP           чисел в correct заметно больше
Заглушки / правдоподобность:
  SHORT_DISTR          wrong короче 60% длины correct (показываем label/len/ratio)
  COMMA_GAP            запятых (≈claim'ов) в correct заметно больше
  CARICATURE           wrong несёт маркеры токсичности/абсурда (по группам, со сниппетом)
  INFLATED_CARICATURE  wrong раздут до длины/формы correct, но всё равно карикатурен
Схема/целостность:
  INVALID_CORRECT_COUNT  не ровно один correct:true
  MISSING_LABELS         нет label/order у опций
  EMPTY_TEXT             пустой text (warning)

Severity блока: LOW / MEDIUM / HIGH / CRITICAL.

Метрики на уровне файла
-----------------------
  correct_longest_rate, correct_shortest_rate, correct_most_technical_rate,
  correct_unique_structure_rate, avg_correct_length_rank.

Использование
-------------
  python3 scripts/audit-mcq-parity.py
  python3 scripts/audit-mcq-parity.py --top 25
  python3 scripts/audit-mcq-parity.py -v
  python3 scripts/audit-mcq-parity.py --caricature
  python3 scripts/audit-mcq-parity.py --json-report reports/mcq-parity.json
  python3 scripts/audit-mcq-parity.py --markdown-report reports/mcq-parity.md
  python3 scripts/audit-mcq-parity.py --fail-on critical
  python3 scripts/audit-mcq-parity.py modules/.../mentoring-interview.json
  python3 scripts/audit-mcq-parity.py --selftest

Скрипт read-only: никакого --fix. Финальное решение и переписывание — за человеком.
"""
import argparse
import json
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
SEED = REPO / "modules/quiz-app/src/main/resources/seed/mcq"

# ---- пороги -----------------------------------------------------------------
LEN_AVG_THR = 1.3
LEN_SPREAD_THR = 1.5
SHORT_DISTR_THR = 0.6
COMMA_RATIO_THR = 1.5
COMMA_ABS_THR = 3
WORD_GAP_RATIO = 1.4
WORD_GAP_ABS = 6
SENT_GAP_ABS = 2
BACKTICK_GAP_ABS = 2
NUMBER_GAP_ABS = 3
TECH_GAP_RATIO = 1.5
TECH_GAP_ABS = 3
STRUCT_GAP_ABS = 2

SEVERITY_ORDER = {"NONE": 0, "LOW": 1, "MEDIUM": 2, "HIGH": 3, "CRITICAL": 4}

# ---- структурные маркеры ----------------------------------------------------
ARROW = re.compile(r"→|->|⇒")
ENUM_COLON = re.compile(r":\s*[^:]+[;,].+")          # «X: a; b» / «X: a, b, c»
SEMI = re.compile(r";")
NUM_LIST = re.compile(r"(^|\s)(?:1[\).]\s|шаг\s*1|этап\s*1)", re.IGNORECASE)
CAUSAL = re.compile(r"потому что|так как|поэтому|вследствие|因此|→|->|⇒|, что приводит|приводит к")
LATIN = re.compile(r"[A-Za-z][A-Za-z0-9_.+\-]{1,}")
NUMRE = re.compile(r"\d+(?:[.,]\d+)?")
QUOTED = re.compile(r"«[^»]+»|\"[^\"]+\"")
SENT_SPLIT = re.compile(r"[.!?…]+")

# ---- маркеры карикатуры (Inflated Caricature Distractor), по группам --------
# Вес группы: насколько сам факт наличия маркера выдаёт «написано чтобы не выбрали».
CARICATURE_GROUPS = {
    "absolute": (1, [               # категоричность — слабый сигнал сам по себе
        r"\bвсегда\b", r"\bникогда\b", r"\bни в коем случае\b",
        r"\bабсолютно всем\b", r"\bвсех подряд\b", r"\bвсё подряд\b",
        r"\bunconditionally\b", r"\balways disable\b", r"\bnever use\b",
    ]),
    "toxic_management": (2, [       # «злодейский менеджер»
        r"публично и жёстк", r"публично и жестк", r"\bунльтиматум", r"\bультиматум",
        r"\bотчитать\b", r"прилюдно", r"пристыдить", r"\bнаказать\b",
        r"угрожа", r"\bпригрозить", r"\bзаставить\b", r"заставля", r"насильно",
    ]),
    "absurd_action": (2, [          # очевидно абсурдное действие
        r"\bничего не дела", r"\bпросто игнор", r"\bзабить\b", r"\bнаплева", r"\bплевать\b",
        r"just restart everything", r"ignore the spec", r"\bснести всё\b", r"удалить всё",
    ]),
    "dismissive": (1, [             # обесценивание человека
        r"без амбиц", r"оставить в покое", r"\bбесперспектив", r"не тратить усил",
        r"\bне стоит вним", r"\bне нужен фидбэк",
    ]),
    "fake_reasoning": (1, [         # маркеры псевдо-обоснования
        r"\bякобы\b", r"\bпросто потому что\b", r"\bочевидно же\b", r"мол,",
    ]),
}
COMPILED_CARICATURE = {
    g: (w, re.compile("|".join(pats), re.IGNORECASE)) for g, (w, pats) in CARICATURE_GROUPS.items()
}


# ============================================================================
# метрики опции
# ============================================================================
def opt_metrics(text):
    """Числовые признаки одной опции."""
    t = text or ""
    words = len(t.split())
    sents = len([s for s in SENT_SPLIT.split(t) if s.strip()])
    backticks = t.count("`") // 2
    numbers = len(NUMRE.findall(t))
    arrows = len(ARROW.findall(t))
    semis = t.count(";")
    colons = t.count(":")
    parens = t.count("(")
    quoted = len(QUOTED.findall(t))
    latin = len(LATIN.findall(t))
    commas = t.count(",")
    # композитная тех-плотность
    tech = backticks * 1.5 + numbers + arrows + semis + latin * 0.5 + parens * 0.5
    # формы
    has_sequence = bool(ARROW.search(t))
    has_algorithm = bool(NUM_LIST.search(t))
    has_enum = bool(ENUM_COLON.search(t)) or semis >= 2
    has_causal = bool(CAUSAL.search(t))
    return {
        "len": len(t), "words": words, "sentences": sents, "backticks": backticks,
        "numbers": numbers, "arrows": arrows, "semicolons": semis, "colons": colons,
        "parens": parens, "quoted": quoted, "latin": latin, "commas": commas,
        "tech": tech, "has_sequence": has_sequence, "has_algorithm": has_algorithm,
        "has_enum": has_enum, "has_causal": has_causal,
    }


def struct_markers(text):
    """Набор структурных маркеров строки (для UNIQ_MARKER)."""
    m = set()
    if ARROW.search(text):
        m.add("arrow")
    if ENUM_COLON.search(text):
        m.add("colon-enum")
    if SEMI.search(text):
        m.add("semicolon")
    if text.count("`") // 2 >= 2:
        m.add("multi-backtick")
    if NUM_LIST.search(text):
        m.add("step-list")
    return m


def caricature_scan(text):
    """Группы маркеров карикатуры в строке + сниппеты. Возвращает (groups, score, snippets)."""
    groups = {}
    snippets = []
    score = 0
    for g, (w, rx) in COMPILED_CARICATURE.items():
        found = []
        for mobj in rx.finditer(text or ""):
            found.append(mobj.group(0).lower())
            a = max(0, mobj.start() - 28)
            b = min(len(text), mobj.end() + 28)
            snip = text[a:b].replace("\n", " ").strip()
            snippets.append(f"…{snip}…")
        if found:
            groups[g] = sorted(set(found))
            score += w  # вес группы один раз
    return groups, score, snippets


# ============================================================================
# анализ блока
# ============================================================================
def _norm_options(block):
    opts = block.get("options")
    return opts if isinstance(opts, list) else []


def analyze_block(opts, q_number, block_idx):
    """Полный анализ одного MCQ-блока. Возвращает issue-dict (severity может быть NONE)."""
    reasons = []
    warnings = []
    metrics = []
    for o in opts:
        if "text" not in o or o.get("text") is None:
            warnings.append(f"EMPTY_TEXT order={o.get('order')}")
        metrics.append((o, opt_metrics(o.get("text", ""))))

    corrects = [(o, m) for o, m in metrics if o.get("correct")]
    wrongs = [(o, m) for o, m in metrics if not o.get("correct")]

    # ---- целостность схемы ----
    if len(corrects) != 1:
        reasons.append(f"INVALID_CORRECT_COUNT {len(corrects)}")
    if any(o.get("label") in (None, "") or o.get("order") is None for o, _ in metrics):
        reasons.append("MISSING_LABELS")

    dims = set()           # на каких осях выделяется correct: length/structure/density
    inflated = False
    caric_max = 0
    caric_detail = []      # (label, groups, snippets)

    if len(corrects) == 1 and wrongs:
        co, cm = corrects[0]
        all_len = [m["len"] for _, m in metrics]
        wl = [m["len"] for _, m in wrongs]
        avg_w = sum(wl) / len(wl)

        # --- длина ---
        if avg_w and cm["len"] / avg_w > LEN_AVG_THR:
            reasons.append(f"LEN_AVG {cm['len']/avg_w:.2f}")
            dims.add("length")
        if min(all_len) and max(all_len) / min(all_len) > LEN_SPREAD_THR:
            reasons.append(f"LEN_SPREAD {max(all_len)/min(all_len):.2f}")
        maxw_words = max(m["words"] for _, m in wrongs)
        if cm["words"] >= maxw_words + WORD_GAP_ABS and maxw_words and cm["words"] / maxw_words > WORD_GAP_RATIO:
            reasons.append(f"WORD_COUNT_GAP {cm['words']}vs{maxw_words}")
            dims.add("length")
        maxw_sent = max(m["sentences"] for _, m in wrongs)
        if cm["sentences"] >= maxw_sent + SENT_GAP_ABS and cm["sentences"] >= 3:
            reasons.append(f"SENTENCE_COUNT_GAP {cm['sentences']}vs{maxw_sent}")

        # --- структура: UNIQ_MARKER ---
        cmk = struct_markers(co.get("text", ""))
        wmk = set().union(*(struct_markers(o.get("text", "")) for o, _ in wrongs)) if wrongs else set()
        uniq = cmk - wmk
        if uniq:
            reasons.append("UNIQ_MARKER " + "/".join(sorted(uniq)))
            dims.add("structure")

        # --- структура: «ровно один вариант несёт форму» ---
        for form, label in (("has_sequence", "ONLY_ONE_SEQUENCE_OPTION"),
                            ("has_algorithm", "ONLY_ONE_ALGORITHM_OPTION"),
                            ("has_enum", "ONLY_ONE_ENUMERATION_OPTION"),
                            ("has_causal", "ONLY_ONE_CAUSAL_OPTION")):
            carriers = [(o, m) for o, m in metrics if m[form]]
            if len(carriers) == 1:
                only_o = carriers[0][0]
                is_corr = bool(only_o.get("correct"))
                reasons.append(f"{label} [{only_o.get('label')}]" + ("(correct)" if is_corr else ""))
                if is_corr:
                    dims.add("structure")

        # --- структура: суммарная плотность →/;/: ---
        cstruct = cm["arrows"] + cm["semicolons"] + cm["colons"]
        wstruct = max(m["arrows"] + m["semicolons"] + m["colons"] for _, m in wrongs)
        if cstruct >= wstruct + STRUCT_GAP_ABS and cstruct >= 3:
            reasons.append(f"STRUCTURE_DENSITY_GAP {cstruct}vs{wstruct}")
            dims.add("structure")

        # --- тех. плотность ---
        maxw_tech = max(m["tech"] for _, m in wrongs)
        if cm["tech"] >= maxw_tech + TECH_GAP_ABS and (maxw_tech == 0 or cm["tech"] / maxw_tech > TECH_GAP_RATIO):
            reasons.append(f"TECH_DENSITY_GAP {cm['tech']:.0f}vs{maxw_tech:.0f}")
            dims.add("density")
        maxw_bt = max(m["backticks"] for _, m in wrongs)
        if cm["backticks"] >= maxw_bt + BACKTICK_GAP_ABS:
            reasons.append(f"BACKTICK_GAP {cm['backticks']}vs{maxw_bt}")
            dims.add("density")
        maxw_num = max(m["numbers"] for _, m in wrongs)
        if cm["numbers"] >= maxw_num + NUMBER_GAP_ABS:
            reasons.append(f"NUMBER_GAP {cm['numbers']}vs{maxw_num}")
            dims.add("density")

        # --- запятые (≈claim'ы) ---
        cc = cm["commas"]
        mwc = max(m["commas"] for _, m in wrongs)
        if cc >= mwc + COMMA_ABS_THR and (mwc == 0 or cc / mwc > COMMA_RATIO_THR):
            reasons.append(f"COMMA_GAP {cc}vs{mwc}")

        # --- короткие заглушки (все wrong ниже порога) ---
        for o, m in wrongs:
            if cm["len"] and m["len"] / cm["len"] < SHORT_DISTR_THR:
                reasons.append(f"SHORT_DISTR {o.get('label')} len={m['len']} vs correct len={cm['len']} ratio={m['len']/cm['len']:.2f}")

        # --- карикатура (по всем wrong) ---
        med_len = sorted(m["len"] for _, m in metrics)[len(metrics) // 2]
        for o, m in wrongs:
            groups, sc, snips = caricature_scan(o.get("text", ""))
            if sc > 0:
                caric_detail.append((o.get("label"), groups, snips[:2]))
                caric_max = max(caric_max, sc)
                # INFLATED: длинный (≈ как correct) и при этом карикатурен
                if m["len"] >= max(med_len, 0.8 * cm["len"]) and m["len"] >= 120:
                    inflated = True
        if caric_detail:
            cats = sorted({g.upper() for _, gs, _ in caric_detail for g in gs})
            reasons.append("CARICATURE " + "/".join(cats))
        if inflated:
            reasons.append("INFLATED_CARICATURE")

    # ---- severity ----
    severity = _block_severity(reasons, dims, caric_max, inflated)

    correct_label = corrects[0][0].get("label") if len(corrects) == 1 else None
    lengths = {o.get("label") or f"#{o.get('order')}": m["len"] for o, m in metrics}
    return {
        "q_number": q_number, "block_idx": block_idx, "severity": severity,
        "reasons": reasons, "warnings": warnings,
        "correct_label": correct_label, "lengths": lengths,
        "caricature": [{"label": lbl, "groups": gs, "snippets": sn} for lbl, gs, sn in caric_detail],
    }


def _block_severity(reasons, dims, caric_max, inflated):
    if not reasons:
        return "NONE"
    has_schema = any(r.startswith(("INVALID_CORRECT_COUNT", "MISSING_LABELS")) for r in reasons)
    len_avg = next((float(r.split()[1]) for r in reasons if r.startswith("LEN_AVG ")), 0.0)
    n_core = len(dims)  # length/structure/density на correct
    # CRITICAL
    if n_core >= 3:
        return "CRITICAL"
    if inflated and (caric_max >= 2 or n_core >= 1):
        return "CRITICAL"
    if caric_max >= 4:
        return "CRITICAL"
    # HIGH
    if has_schema:
        return "HIGH"
    if n_core >= 2:
        return "HIGH"
    if inflated:
        return "HIGH"
    if caric_max >= 3:
        return "HIGH"
    if len_avg >= 1.5:
        return "HIGH"
    # MEDIUM
    if n_core == 1:
        return "MEDIUM"
    if caric_max >= 2:
        return "MEDIUM"
    if any(r.startswith("SHORT_DISTR") for r in reasons):
        return "MEDIUM"
    if len(reasons) >= 2:
        return "MEDIUM"
    # LOW
    return "LOW"


# ============================================================================
# анализ файла
# ============================================================================
def load_json(path):
    return json.loads(path.read_text(encoding="utf-8"))


def is_mcq_seeder(data):
    return isinstance(data, dict) and isinstance(data.get("questions"), list)


def analyze_file(path):
    """Возвращает dict отчёта по файлу или поднимает исключение/возвращает skip-маркер."""
    data = load_json(path)
    if not is_mcq_seeder(data):
        return {"path": str(path), "skipped": "not-mcq-seeder"}

    issues = []
    nblocks = 0
    longest = shortest = most_tech = uniq_struct = 0
    rank_sum = 0
    for q in data.get("questions", []):
        for bi, b in enumerate(q.get("blocks", []) or []):
            opts = _norm_options(b)
            if len(opts) < 2:
                continue
            nblocks += 1
            res = analyze_block(opts, q.get("q_number"), bi)
            # file-level метрики (по correct)
            ci = next((i for i, o in enumerate(opts) if o.get("correct")), None)
            if sum(1 for o in opts if o.get("correct")) == 1:
                co = opts[ci]
                lens = [len(o.get("text", "") or "") for o in opts]
                techs = [opt_metrics(o.get("text", ""))["tech"] for o in opts]
                cl = lens[ci]
                if cl == max(lens):
                    longest += 1
                if cl == min(lens):
                    shortest += 1
                if techs[ci] == max(techs):
                    most_tech += 1
                cmk = struct_markers(co.get("text", ""))
                wmk = set().union(*(struct_markers(o.get("text", "")) for o in opts if not o.get("correct"))) if len(opts) > 1 else set()
                if cmk - wmk:
                    uniq_struct += 1
                # ранг correct по длине (1=самый короткий .. N=самый длинный)
                rank = sorted(range(len(opts)), key=lambda i: lens[i]).index(ci) + 1
                rank_sum += rank / len(opts)
            if res["severity"] != "NONE":
                issues.append(res)

    rate = longest / nblocks if nblocks else 0.0
    file_sev = "NONE"
    for it in issues:
        if SEVERITY_ORDER[it["severity"]] > SEVERITY_ORDER[file_sev]:
            file_sev = it["severity"]
    return {
        "path": str(path),
        "rel": str(path.relative_to(REPO)) if str(path).startswith(str(REPO)) else str(path),
        "blocks": nblocks,
        "flags": len(issues),
        "severity": file_sev,
        "correct_longest_rate": round(rate, 3),
        "correct_shortest_rate": round(shortest / nblocks, 3) if nblocks else 0.0,
        "correct_most_technical_rate": round(most_tech / nblocks, 3) if nblocks else 0.0,
        "correct_unique_structure_rate": round(uniq_struct / nblocks, 3) if nblocks else 0.0,
        "avg_correct_length_rank": round(rank_sum / nblocks, 3) if nblocks else 0.0,
        "issues": issues,
    }


def collect_targets(paths):
    targets = []
    missing = []
    roots = [Path(p) for p in paths] if paths else [SEED]
    for r in roots:
        rp = r if r.is_absolute() else (Path.cwd() / r)
        rp = rp.resolve()
        if not rp.exists():
            missing.append(str(r))
            continue
        if rp.is_dir():
            targets += sorted(rp.rglob("*.json"))
        elif rp.suffix == ".json":
            targets.append(rp)
        else:
            missing.append(str(r))
    return targets, missing


def analyze_all(paths):
    targets, missing = collect_targets(paths)
    for m in missing:
        print(f"⚠ путь не найден или не .json: {m}", file=sys.stderr)
    reports = []
    for p in targets:
        try:
            rep = analyze_file(p)
        except json.JSONDecodeError as e:
            print(f"⚠ невалидный JSON: {p}: {e}", file=sys.stderr)
            continue
        except Exception as e:  # noqa
            print(f"⚠ ошибка при разборе {p}: {e}", file=sys.stderr)
            continue
        if rep.get("skipped"):
            if "--verbose-skip" in sys.argv:
                print(f"· пропуск ({rep['skipped']}): {p}", file=sys.stderr)
            continue
        reports.append(rep)
    return reports


# ============================================================================
# рендеры
# ============================================================================
def render_human(reports, verbose, top):
    rows = [r for r in reports if r["flags"] or r["severity"] != "NONE"]
    rows.sort(key=lambda r: (-SEVERITY_ORDER[r["severity"]], -r["flags"]))
    shown = rows[:top] if top else rows
    if verbose:
        for r in shown:
            if not r["issues"]:
                continue
            print(f"\n## {r['rel']}  blocks={r['blocks']}  severity={r['severity']}  "
                  f"longest={r['correct_longest_rate']:.0%} most_tech={r['correct_most_technical_rate']:.0%}")
            for it in r["issues"]:
                print(f"   Q{it['q_number']} [{it['severity']}] corr={it['correct_label']}: {', '.join(it['reasons'])}")
    print("\n=== кандидаты на ручную правку (severity-first) ===")
    print(f"{'sev':>8} {'flags':>5} {'long':>5} {'tech':>5}  file")
    counts = {"CRITICAL": 0, "HIGH": 0, "MEDIUM": 0, "LOW": 0}
    for r in shown:
        counts[r["severity"]] = counts.get(r["severity"], 0) + 1
        mark = " ⚠LONGEST" if (r["blocks"] >= 6 and r["correct_longest_rate"] > 0.5) else ""
        print(f"{r['severity']:>8} {r['flags']:>5} {r['correct_longest_rate']:>4.0%} "
              f"{r['correct_most_technical_rate']:>4.0%}  {r['rel']}{mark}")
    total_blocks = sum(r["blocks"] for r in reports)
    total_flags = sum(r["flags"] for r in reports)
    print(f"\nфайлов: {len(reports)}; с флагами: {len(rows)}; блоков: {total_blocks}; флагов-блоков: {total_flags}")
    print(f"severity файлов — CRITICAL: {counts['CRITICAL']}, HIGH: {counts['HIGH']}, "
          f"MEDIUM: {counts['MEDIUM']}, LOW: {counts['LOW']}")
    print("Скрипт только находит кандидатов — финальное решение за человеком.")


def render_caricature(reports, verbose):
    rows = []
    for r in reports:
        car = [it for it in r["issues"] if it["caricature"]]
        if car:
            rows.append((r, car))
    rows.sort(key=lambda x: -len(x[1]))
    if verbose:
        for r, car in rows:
            print(f"\n## {r['rel']}  blocks={r['blocks']}")
            for it in car:
                for c in it["caricature"]:
                    cats = "/".join(g.upper() for g in c["groups"])
                    markers = ", ".join(m for ms in c["groups"].values() for m in ms)
                    snip = c["snippets"][0] if c["snippets"] else ""
                    print(f"   Q{it['q_number']} [{c['label']}]: {cats} — {markers}")
                    if snip:
                        print(f"        {snip}")
    print(f"\n=== Inflated Caricature Distractor — кандидаты (Plausibility Parity) ===")
    print(f"{'blocks':>6}  file")
    total = 0
    for r, car in rows:
        total += len(car)
        print(f"{len(car):>6}  {r['rel']}")
    print(f"\nфайлов с маркерами: {len(rows)}; блоков-кандидатов: {total}")
    print("Скрипт только находит кандидатов — переписывание дистрактора за человеком.")


def build_json_report(reports):
    files = []
    for r in reports:
        files.append({
            "path": r["rel"], "blocks": r["blocks"], "flags": r["flags"],
            "severity": r["severity"],
            "correct_longest_rate": r["correct_longest_rate"],
            "correct_shortest_rate": r["correct_shortest_rate"],
            "correct_most_technical_rate": r["correct_most_technical_rate"],
            "correct_unique_structure_rate": r["correct_unique_structure_rate"],
            "avg_correct_length_rank": r["avg_correct_length_rank"],
            "issues": [
                {
                    "q_number": it["q_number"], "block_idx": it["block_idx"],
                    "severity": it["severity"], "reasons": it["reasons"],
                    "options": {"correct_label": it["correct_label"], "lengths": it["lengths"]},
                    "caricature": it["caricature"],
                } for it in r["issues"]
            ],
        })
    files.sort(key=lambda f: (-SEVERITY_ORDER[f["severity"]], -f["flags"]))
    return {
        "summary": {
            "files_scanned": len(reports),
            "blocks_scanned": sum(r["blocks"] for r in reports),
            "files_with_flags": sum(1 for r in reports if r["flags"]),
            "blocks_with_flags": sum(r["flags"] for r in reports),
            "severity_files": {
                s: sum(1 for r in reports if r["severity"] == s)
                for s in ("CRITICAL", "HIGH", "MEDIUM", "LOW")
            },
        },
        "files": files,
    }


def render_markdown(reports):
    rep = build_json_report(reports)
    s = rep["summary"]
    out = ["# MCQ Parity Audit Report", "", "## Summary", "",
           "| Metric | Value |", "|---|---|",
           f"| Files scanned | {s['files_scanned']} |",
           f"| Blocks scanned | {s['blocks_scanned']} |",
           f"| Files with flags | {s['files_with_flags']} |",
           f"| Blocks with flags | {s['blocks_with_flags']} |",
           f"| CRITICAL files | {s['severity_files']['CRITICAL']} |",
           f"| HIGH files | {s['severity_files']['HIGH']} |",
           f"| MEDIUM files | {s['severity_files']['MEDIUM']} |",
           f"| LOW files | {s['severity_files']['LOW']} |",
           "", "## Top problematic files", "",
           "| Severity | Flags | Correct longest | Most technical | File |",
           "|---|---:|---:|---:|---|"]
    top = [f for f in rep["files"] if f["flags"]][:40]
    for f in top:
        out.append(f"| {f['severity']} | {f['flags']} | {f['correct_longest_rate']:.0%} | "
                   f"{f['correct_most_technical_rate']:.0%} | `{f['path']}` |")
    out += ["", "## Issues", ""]
    for f in top:
        out.append(f"### `{f['path']}`")
        out.append("")
        out.append("| Q | Block | Severity | Reasons | Notes |")
        out.append("|---|---:|---|---|---|")
        for it in f["issues"]:
            notes = ""
            if it["caricature"]:
                notes = "; ".join(f"{c['label']}: {'/'.join(g.upper() for g in c['groups'])}" for c in it["caricature"])
            reasons = ", ".join(it["reasons"]).replace("|", "\\|")
            out.append(f"| {it['q_number']} | {it['block_idx']} | {it['severity']} | {reasons} | {notes} |")
        out.append("")
    return "\n".join(out) + "\n"


# ============================================================================
# обратная совместимость (используется build_plan_reset.py и др.)
# ============================================================================
def audit_file(path):
    """Совместимый интерфейс: (flagged[(q_number, reasons)], nblocks, correct_longest_rate)."""
    rep = analyze_file(Path(path))
    if rep.get("skipped"):
        return [], 0, 0.0
    flagged = [(it["q_number"], it["reasons"]) for it in rep["issues"]]
    return flagged, rep["blocks"], rep["correct_longest_rate"]


def audit_file_caricature(path):
    """Совместимый интерфейс: (flagged[(q_number, [(label,[markers])])], nblocks)."""
    rep = analyze_file(Path(path))
    if rep.get("skipped"):
        return [], 0
    flagged = []
    for it in rep["issues"]:
        if it["caricature"]:
            hits = [(c["label"], sorted(m for ms in c["groups"].values() for m in ms)) for c in it["caricature"]]
            flagged.append((it["q_number"], hits))
    return flagged, rep["blocks"]


# ============================================================================
# self-test (синтетические фикстуры — детекторы обязаны сработать)
# ============================================================================
def selftest():
    ok = True

    def blk(opts):
        return analyze_block(opts, 1, 0)

    # 1. length+structure+density tell на correct → CRITICAL
    r = blk([
        {"order": 0, "label": "A", "text": "Кэш просто хранит данные в памяти.", "correct": False},
        {"order": 1, "label": "B", "text": "Кэш ускоряет чтение.", "correct": False},
        {"order": 2, "label": "C", "text": "Кэш — это база данных.", "correct": False},
        {"order": 3, "label": "D", "correct": True,
         "text": "Чтение проходит так: `client` → `cache` (TTL=300; LRU), при miss → `db`, "
                 "затем запись обратно в `cache`; счётчики `hits`/`misses` (≥0.9 hit-rate, p99=5ms)."},
    ])
    assert "LEN_AVG" in " ".join(r["reasons"]), r["reasons"]
    assert r["severity"] in ("HIGH", "CRITICAL"), r["severity"]

    # 2. инфлированная карикатура (длинный wrong с токсичными маркерами)
    r2 = blk([
        {"order": 0, "label": "A", "correct": False,
         "text": "Нужно публично и жёстко отчитать человека на общей встрече, добавить ультиматум "
                 "и заставить его всегда отчитываться, не спрашивая его мнения вообще никогда."},
        {"order": 1, "label": "B", "text": "Дать приватный фидбэк один на один, спокойно разобрать кейс и план.", "correct": True},
        {"order": 2, "label": "C", "text": "Сразу написать жалобу в HR без разговора с человеком.", "correct": False},
        {"order": 3, "label": "D", "text": "Игнорировать проблему и просто ждать пока само пройдёт.", "correct": False},
    ])
    rj = " ".join(r2["reasons"])
    assert "CARICATURE" in rj, r2["reasons"]
    assert "INFLATED_CARICATURE" in rj, r2["reasons"]

    # 3. короткая заглушка
    r3 = blk([
        {"order": 0, "label": "A", "correct": True,
         "text": "Идемпотентность означает, что повторный POST с тем же ключом не создаёт дубль, "
                 "а возвращает результат первой операции — сервер хранит ключ и ответ."},
        {"order": 1, "label": "B", "text": "Это кэш.", "correct": False},
        {"order": 2, "label": "C", "text": "Это всегда GET.", "correct": False},
        {"order": 3, "label": "D", "text": "Это про SQL.", "correct": False},
    ])
    assert any(x.startswith("SHORT_DISTR") for x in r3["reasons"]), r3["reasons"]

    # 4. невалидный correct count
    r4 = blk([
        {"order": 0, "label": "A", "text": "x", "correct": True},
        {"order": 1, "label": "B", "text": "y", "correct": True},
        {"order": 2, "label": "C", "text": "z", "correct": False},
        {"order": 3, "label": "D", "text": "w", "correct": False},
    ])
    assert any(x.startswith("INVALID_CORRECT_COUNT") for x in r4["reasons"]), r4["reasons"]

    # 5. чистый блок → NONE
    r5 = blk([
        {"order": 0, "label": "A", "text": "Поток A блокируется на мониторе и ждёт сигнала.", "correct": False},
        {"order": 1, "label": "B", "text": "Поток B захватывает лок и освобождает его сразу.", "correct": True},
        {"order": 2, "label": "C", "text": "Поток C крутится в busy-wait, не отдавая процессор.", "correct": False},
        {"order": 3, "label": "D", "text": "Поток D переходит в состояние ожидания по таймеру.", "correct": False},
    ])
    assert r5["severity"] in ("NONE", "LOW"), (r5["severity"], r5["reasons"])

    print("selftest: OK (5 фикстур: length+struct+density, inflated-caricature, short-stub, "
          "invalid-correct, clean)")
    return ok


# ============================================================================
# CLI
# ============================================================================
def main():
    ap = argparse.ArgumentParser(
        description="Read-only аудит угадываемости correct в MCQ JSON-сидерах (Option/Structural/Plausibility Parity).",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="Скрипт ничего не переписывает (нет --fix); только находит кандидатов на ручную правку.")
    ap.add_argument("paths", nargs="*", help="файлы/каталоги (по умолчанию весь seed/mcq)")
    ap.add_argument("-v", "--verbose", action="store_true", help="подробно по каждому блоку")
    ap.add_argument("--top", type=int, metavar="N", help="показать только N худших файлов")
    ap.add_argument("--caricature", action="store_true", help="режим только caricature/Plausibility")
    ap.add_argument("--json-report", metavar="PATH", help="сохранить машинный JSON-отчёт")
    ap.add_argument("--markdown-report", metavar="PATH", help="сохранить Markdown-отчёт")
    ap.add_argument("--fail-on", choices=["high", "critical"], help="exit 1 при наличии HIGH/CRITICAL")
    ap.add_argument("--selftest", action="store_true", help="прогнать встроенные фикстуры и выйти")
    args = ap.parse_args()

    if args.selftest:
        selftest()
        return 0

    reports = analyze_all(args.paths)

    if args.json_report:
        outp = Path(args.json_report)
        outp.parent.mkdir(parents=True, exist_ok=True)
        outp.write_text(json.dumps(build_json_report(reports), ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"JSON-отчёт → {outp}")
    if args.markdown_report:
        outp = Path(args.markdown_report)
        outp.parent.mkdir(parents=True, exist_ok=True)
        outp.write_text(render_markdown(reports), encoding="utf-8")
        print(f"Markdown-отчёт → {outp}")

    if args.caricature:
        render_caricature(reports, args.verbose)
    elif not (args.json_report or args.markdown_report) or args.verbose:
        render_human(reports, args.verbose, args.top)

    if args.fail_on:
        threshold = "CRITICAL" if args.fail_on == "critical" else "HIGH"
        worst = max((SEVERITY_ORDER[r["severity"]] for r in reports), default=0)
        if worst >= SEVERITY_ORDER[threshold]:
            n = sum(1 for r in reports if SEVERITY_ORDER[r["severity"]] >= SEVERITY_ORDER[threshold])
            print(f"\n✗ --fail-on {args.fail_on}: {n} файл(ов) с severity ≥ {threshold}", file=sys.stderr)
            return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
