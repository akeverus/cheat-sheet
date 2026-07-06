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

Человекочитаемость русской прозы (отдельная ось — text + sections каждой опции)
------------------------------------------------------------------------------
Не про «угадываемость», а про качество текста для живого читателя-человека.
Срабатывает на ответах, которые тяжело/неприятно читать по-русски:
  LOW_CYRILLIC   доля кириллицы среди букв (после снятия `code`) < 0.5 → непереведённая
                 англоязычная проза (термины в backticks из расчёта исключаются)
  ENGLISH_RUN    ≥4 латинских слов подряд вне backticks И со служебным англ. словом
                 (the/is/of/to…) → непереведённое английское предложение, не имя продукта
  RUS_READABILITY_CLICHE             вода/academic-tone из стоп-листа
  RUS_READABILITY_LONG_SENTENCE      предложение длиннее 45 слов → стена текста / run-on
  RUS_READABILITY_TOO_MANY_ARROWS    слишком много стрелок в prose-варианте
  RUS_READABILITY_TOO_MANY_SEMICOLONS слишком много `;` в одном варианте/предложении
  RUS_READABILITY_MACHINE_STYLE      машинная цепочка коротких фрагментов через стрелки
  RUS_READABILITY_OVERSTRUCTURED     вариант перегружен искусственными разделителями
  RUS_READABILITY_PUNCT              двойной пробел / пробел перед знаком
Readability severity блока: LOW / MEDIUM / HIGH (язык важнее косметики). Ось независима от
parity-severity — не меняет существующие parity-флаги, считается и отображается отдельно.

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
  python3 scripts/audit-mcq-parity.py --readability
  python3 scripts/audit-mcq-parity.py --json-report reports/mcq-parity.json
  python3 scripts/audit-mcq-parity.py --markdown-report reports/mcq-parity.md
  python3 scripts/audit-mcq-parity.py --fail-on critical
  python3 scripts/audit-mcq-parity.py modules/.../mentoring-interview.json
  python3 scripts/audit-mcq-parity.py --verbose-skip path/to/dir
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
EXPECTED_LABELS = ("A", "B", "C", "D")

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

# ---- человекочитаемость русской прозы ---------------------------------------
# Read-only эвристики: находят кандидатов, где ответ плохо читается по-русски —
# непереведённый английский, вода/academic-tone, run-on предложения, склейка пунктуации.
CYR_LETTER = re.compile(r"[а-яёА-ЯЁ]")
LAT_LETTER = re.compile(r"[A-Za-z]")
BACKTICK_SPAN = re.compile(r"`[^`]*`")
URL_RE = re.compile(r"https?://\S+")
LAT_WORD = re.compile(r"[A-Za-z][A-Za-z'’\-]*")
QUOTE_SPAN = re.compile(r"«[^»]*»|\"[^\"]*\"|“[^”]*”")   # намеренные цитаты — из языка-замера исключаем
PAREN_SPAN = re.compile(r"\([^)]*\)")                    # скобки — обычно расшифровка акронима/глосс (DORA, SQALE)
DOUBLE_SPACE_RE = re.compile(r"\S {2,}\S")               # двойной+ пробел внутри текста
SPACE_BEFORE_PUNCT_RE = re.compile(r"[A-Za-zА-Яа-яёЁ]\s[,.;!?](?:\s|$)")  # пробел ПЕРЕД знаком после слова

# Калибровано по адверс-выборке (см. wf_rdb_calibrate): пороги выставлены на ВЫСОКУЮ ТОЧНОСТЬ —
# корпус легитимно code-switch'ит (имена продуктов/типов/эндпоинтов, term-фразы «single point of
# failure»), поэтому сырая доля латиницы/длина run наказывали нормальные русские шпаргалки.
# Истинное срабатывание = непереведённая английская ПРОЗА со служебными словами, а не цепочка имён.
CYR_RATIO_THR = 0.5         # доля кириллицы среди букв (после снятия кода/цитат/скобок) ниже → кандидат
MIN_LETTERS_FOR_RATIO = 16  # не судить о языке слишком коротких фрагментов
MIN_LAT_FOR_RATIO = 12      # и только если латиницы реально много (не один термин)
LOW_CYR_MIN_STOP = 3        # LOW_CYRILLIC только при ≥3 строчных англ. служебных (реальная англ. проза)
ENGLISH_RUN_THR = 5         # ≥N подряд латинских слов в run'е
ENGLISH_RUN_MIN_STOP = 2    # И ≥2 строчных служебных В ПРЕДЕЛАХ run'а (отсекает «single point of failure», расшифровки)
LONG_SENTENCE_WORDS = 45    # предложение длиннее → кандидат на run-on (если это НЕ структурное перечисление)

# служебные англ. слова — их наличие в run'е отличает предложение от цепочки имён продуктов
ENGLISH_STOP = {
    "the", "a", "an", "is", "are", "was", "were", "of", "to", "and", "for", "with",
    "that", "this", "in", "on", "it", "as", "by", "or", "be", "from", "you", "your",
    "we", "if", "not", "but", "can", "will", "should", "would", "which", "when", "how",
    "what", "at", "its", "their", "they", "than", "then", "so", "do", "does", "has", "have",
}

# FILLER — только заведомо ПУСТЫЕ academic-зачины (узко, по калибровке: русское «важно понимать, что X»
# почти всегда несёт смысловое придаточное, в отличие от англо-«it is important to note»).
FILLER_PHRASES = [
    "важно отметить", "стоит отметить", "следует отметить", "нельзя не отметить",
    "хотелось бы отметить", "необходимо отметить", "стоит заметить", "стоит упомянуть",
    "давайте рассмотрим", "в этом разделе", "в данном разделе", "как мы знаем", "как известно,",
]
FILLER_RE = re.compile("|".join(re.escape(p) for p in FILLER_PHRASES), re.IGNORECASE)
# поля, где «важно понимать, что…»/прямая речь — норма; FILLER там не считаем
FILLER_SKIP_KEYS = {"source_of_confusion", "edge_cases", "when_to_apply", "if_it_were_true"}
# маркеры структурного перечисления — «длинное предложение» с ними читается как список, не run-on
ENUM_NUMBERING = re.compile(r"\(\d\)|\b\d\)|\bшаг\s*\d|\bэтап\s*\d", re.IGNORECASE)
MACHINE_CHAIN_MIN_ARROWS = 2
TOO_MANY_ARROWS_THR = 3
TOO_MANY_SEMICOLONS_THR = 4
OVERSTRUCTURED_DENSITY_THR = 6


def _readability_issue(code, detail, where, legacy_code=None):
    issue = {"code": code, "detail": detail, "where": where}
    if legacy_code:
        issue["legacy_code"] = legacy_code
    return issue


def _strip_code(t):
    """Убрать `code`-спаны и URL — они латинские по природе и не должны судить о языке прозы."""
    t = BACKTICK_SPAN.sub(" ", t or "")
    t = URL_RE.sub(" ", t)
    return t


def _mask_code(t):
    """Заместить `code`-спаны и URL одним сентинелом «·» БЕЗ изменения пробелов вокруг —
    для проверок пунктуации (двойной пробел / пробел перед знаком)."""
    t = BACKTICK_SPAN.sub("·", t or "")
    t = URL_RE.sub("·", t)
    return t


def _looks_like_enumeration(seg):
    """«Длинное предложение» — на деле структурный список? (двоеточие+разделители / нумерация /
    параллелизм). Такой текст читается по пунктам, не run-on — гасим LONG_SENTENCE."""
    if ENUM_NUMBERING.search(seg):
        return True
    has_colon = ":" in seg
    commas = seg.count(",")
    semis = seg.count(";")
    dashes = seg.count(" — ") + seg.count(" – ")
    glosses = seg.count("(")  # пояснения-глоссы в скобках на пункт
    if has_colon and (commas >= 3 or semis >= 2):
        return True
    if semis >= 3 or dashes >= 2 or glosses >= 3 or commas >= 6:
        return True
    return False


def _looks_like_machine_chain(text):
    """Искусственная цепочка вида «вспомнил → оценил → озвучил».

    Алгоритмы с `code`, номерами и длинными поясняющими шагами не считаем машинным стилем.
    """
    prose = _strip_code(text)
    if len(ARROW.findall(prose)) < MACHINE_CHAIN_MIN_ARROWS:
        return False
    if NUMRE.search(prose) or "`" in text:
        return False
    parts = [p.strip(" .,;:()") for p in ARROW.split(prose) if p.strip(" .,;:()")]
    if len(parts) < 3:
        return False
    short_parts = sum(1 for p in parts if 1 <= len(p.split()) <= 6)
    has_russian = bool(CYR_LETTER.search(prose))
    return has_russian and short_parts >= 3 and short_parts / len(parts) >= 0.75


def readability_scan(text, where, key=None):
    """Эвристики читаемости русской прозы для одной строки. → список {code, detail, where}.
    Калибровано на высокую точность (FP-выборка): язык — только реальная англ. проза со
    служебными словами; длинное предложение — только без структуры списка; filler — узкий стоп-лист."""
    out = []
    t = text or ""
    if not t.strip():
        return out
    prose = _strip_code(t)
    # для языка-замера дополнительно убираем цитаты («…») и скобки (расшифровки акронимов/глоссы)
    lang_prose = PAREN_SPAN.sub(" ", QUOTE_SPAN.sub(" ", prose))

    # язык-эвристики: один проход. Стоп-слово засчитываем ТОЛЬКО для строчного токена — отличает
    # настоящую англ. прозу («this is the default») от Title-Case расшифровок и имён продуктов.
    # ENGLISH_RUN требует В ПРЕДЕЛАХ run'а: ≥2 служебных строчных И ≥2 СОДЕРЖАТЕЛЬНЫХ строчных слова
    # (не-стоп, ≥3 букв) — это отсекает имена-из-Title-Case с одиночными строчными связками
    # («Best Time to Buy and Sell Stock», «Lead Time for Changes», «out of the box»): там content≈0.
    longest_run = run = run_stops = run_content = 0
    flagged_run = False
    n_stop = 0
    for tok in lang_prose.split():
        bare = tok.strip(".,;:!?()«»\"'’[]{}")
        w = bare.lower()
        if w and LAT_WORD.fullmatch(w):
            run += 1
            if bare == w:                              # bare == w → токен уже строчный
                if w in ENGLISH_STOP:
                    run_stops += 1
                    n_stop += 1
                elif len(w) >= 3:
                    run_content += 1
            longest_run = max(longest_run, run)
            if run >= ENGLISH_RUN_THR and run_stops >= ENGLISH_RUN_MIN_STOP and run_content >= 2:
                flagged_run = True
        else:
            run = run_stops = run_content = 0

    # 1. LOW_CYRILLIC: латиница доминирует И есть ≥3 строчных служебных слова (реальная англ. проза,
    #    а не цепочка имён/терминов). Без stopword-порога давало ~78% ложных на этом корпусе.
    cyr = len(CYR_LETTER.findall(lang_prose))
    lat = len(LAT_LETTER.findall(lang_prose))
    letters = cyr + lat
    if letters >= MIN_LETTERS_FOR_RATIO and lat >= MIN_LAT_FOR_RATIO:
        ratio = cyr / letters
        if ratio < CYR_RATIO_THR and n_stop >= LOW_CYR_MIN_STOP:
            out.append(_readability_issue(
                "LOW_CYRILLIC",
                f"cyr={ratio:.2f} (lat={lat}, cyr={cyr}, en-stop={n_stop})",
                where,
            ))

    # 2. ENGLISH_RUN: ≥5 латинских слов подряд И ≥2 служебных строчных в этом run'е
    if flagged_run:
        out.append(_readability_issue("ENGLISH_RUN", f"{longest_run} англ. слов подряд (англ. предложение)", where))

    # 3. вода / academic-tone (узкий стоп-лист; не в полях-исключениях; вне цитат)
    if key not in FILLER_SKIP_KEYS:
        fil = sorted({m.group(0).lower() for m in FILLER_RE.finditer(QUOTE_SPAN.sub(" ", t))})
        if fil:
            out.append(_readability_issue("RUS_READABILITY_CLICHE", ", ".join(fil), where, legacy_code="FILLER_PHRASE"))

    # 4. run-on предложение: длинное И НЕ структурное перечисление (двоеточие+разделители/нумерация)
    for seg in SENT_SPLIT.split(prose):
        if len(seg.split()) > LONG_SENTENCE_WORDS and not _looks_like_enumeration(seg):
            out.append(_readability_issue(
                "RUS_READABILITY_LONG_SENTENCE",
                f"{len(seg.split())} слов в предложении без структуры списка",
                where,
                legacy_code="LONG_SENTENCE",
            ))
            break

    # 5. искусственная структурность: стрелки, semicolon-цепочки, машинные «алгоритмы» в прозе.
    arrow_count = len(ARROW.findall(prose))
    semicolon_count = prose.count(";")
    colon_count = prose.count(":")
    structure_density = arrow_count + semicolon_count + colon_count
    if arrow_count >= TOO_MANY_ARROWS_THR:
        out.append(_readability_issue(
            "RUS_READABILITY_TOO_MANY_ARROWS",
            f"стрелок={arrow_count}",
            where,
        ))
    if semicolon_count >= TOO_MANY_SEMICOLONS_THR or any(seg.count(";") >= 3 for seg in SENT_SPLIT.split(prose)):
        out.append(_readability_issue(
            "RUS_READABILITY_TOO_MANY_SEMICOLONS",
            f"semicolon={semicolon_count}",
            where,
        ))
    if _looks_like_machine_chain(t):
        out.append(_readability_issue(
            "RUS_READABILITY_MACHINE_STYLE",
            "короткие prose-фрагменты соединены стрелками как механическая цепочка",
            where,
        ))
    if structure_density >= OVERSTRUCTURED_DENSITY_THR or (arrow_count >= 2 and semicolon_count >= 2):
        out.append(_readability_issue(
            "RUS_READABILITY_OVERSTRUCTURED",
            f"structure_density={structure_density} (arrows={arrow_count}, semicolons={semicolon_count}, colons={colon_count})",
            where,
        ))

    # 6. пунктуация: только двойной пробел / пробел перед знаком после СЛОВА (по masked коду).
    #    Склейку «слово,слово» убрали — на этом корпусе это сплошь нотация (C(n,k), X,Y,Z, Map<K,V>).
    punct_src = _mask_code(t)
    glitches = []
    if DOUBLE_SPACE_RE.search(punct_src):
        glitches.append("двойной пробел")
    if SPACE_BEFORE_PUNCT_RE.search(punct_src):
        glitches.append("пробел перед знаком")
    if glitches:
        out.append(_readability_issue("RUS_READABILITY_PUNCT", "; ".join(glitches), where, legacy_code="PUNCT"))

    return out


def _readability_severity(rdb):
    """Severity ряда readability-флагов: язык важнее косметики."""
    if not rdb:
        return "NONE"
    codes = [x["code"] for x in rdb]
    lang = sum(1 for c in codes if c in ("LOW_CYRILLIC", "ENGLISH_RUN"))
    rus_codes = [c for c in codes if c.startswith("RUS_READABILITY_")]
    if lang >= 2:
        return "HIGH"
    if lang == 1:
        return "MEDIUM"
    if "RUS_READABILITY_LONG_SENTENCE" in codes and "RUS_READABILITY_CLICHE" in codes:
        return "MEDIUM"
    if "RUS_READABILITY_OVERSTRUCTURED" in codes and (
        "RUS_READABILITY_MACHINE_STYLE" in codes or "RUS_READABILITY_TOO_MANY_ARROWS" in codes
    ):
        return "MEDIUM"
    if len(rus_codes) >= 3 or len(codes) >= 3:
        return "MEDIUM"
    return "LOW"


# секции не-прозы: ссылки/слаги и код легитимно латинские — язык/пунктуацию по ним не судим
NON_PROSE_SECTIONS = {"related", "example"}


def block_readability(opts):
    """Собрать readability-флаги по опциям блока (text + прозаические строковые sections)."""
    rdb = []
    for o in opts:
        lbl = o.get("label") or f"#{o.get('order')}"
        rdb += readability_scan(o.get("text", ""), f"{lbl}.text", key="text")
        secs = o.get("sections")
        if isinstance(secs, dict):
            for k, v in secs.items():
                if isinstance(v, str) and k not in NON_PROSE_SECTIONS:
                    rdb += readability_scan(v, f"{lbl}.{k}", key=k)
    return rdb


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


def format_scan(corrects, wrongs):
    """Format Parity: correct не должен быть УНИКАЛЬНЫМ выбросом по формат-оси
    (правило пользователя «тире в общем месте, один формат у всех вариантов»).
    Флагуем только когда correct — единственный, кто отличается от ВСЕХ дистракторов
    по конкретной пунктуационной оси (или единственный, кто её не несёт). Высокая точность."""
    reasons = []
    if len(corrects) != 1 or len(wrongs) < 2:
        return reasons
    c = (corrects[0][0].get("text") or "").strip()
    ws = [(o.get("text") or "").strip() for o, _ in wrongs]
    if not c or not all(ws):
        return reasons

    def uniq(axis):
        cv = axis(c)
        wv = [axis(w) for w in ws]
        if cv and not any(wv):
            return "corr-only"
        if (not cv) and all(wv):
            return "corr-lacks"
        return None

    # Только оси, НЕ покрытые UNIQ_MARKER (colon/semicolon-enum уже там): тире, точка, открывающий backtick.
    checks = [
        ("FORMAT_DASH", lambda t: " — " in t or "—" in t),          # тире (em-dash) «в общем месте»
        ("FORMAT_PERIOD", lambda t: t.endswith(".")),               # завершающая точка — у всех или ни у кого
        ("FORMAT_OPEN", lambda t: t.startswith("`")),               # открывающий backtick-идентификатор
    ]
    for name, axis in checks:
        verdict = uniq(axis)
        if verdict:
            reasons.append(f"{name} {verdict}")
    return reasons


# ============================================================================
# анализ блока
# ============================================================================
def _norm_options(block):
    opts = block.get("options")
    return opts if isinstance(opts, list) else []


def analyze_block(opts, q_number, block_idx, option_error=None):
    """Полный анализ одного MCQ-блока. Возвращает issue-dict (severity может быть NONE)."""
    reasons = []
    warnings = []
    metrics = []
    if option_error:
        reasons.append(option_error)
    if len(opts) != 4:
        reasons.append(f"INVALID_OPTION_COUNT {len(opts)}")
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
    if len(opts) == 4:
        orders = [o.get("order") for o, _ in metrics]
        labels = [o.get("label") for o, _ in metrics]
        if not all(isinstance(x, int) for x in orders) or sorted(orders) != [0, 1, 2, 3]:
            reasons.append("INVALID_ORDER_SEQUENCE " + ",".join(str(x) for x in orders))
        if set(labels) != set(EXPECTED_LABELS):
            reasons.append("INVALID_LABEL_SEQUENCE " + ",".join(str(x) for x in labels))
        mismatches = []
        for o, _ in metrics:
            order = o.get("order")
            label = o.get("label")
            if isinstance(order, int) and 0 <= order < len(EXPECTED_LABELS) and label != EXPECTED_LABELS[order]:
                mismatches.append(f"{label}@{order}")
        if mismatches:
            reasons.append("LABEL_ORDER_MISMATCH " + ",".join(mismatches))

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

        # --- Format Parity (пунктуационный формат-tell: correct — уникальный выброс) ---
        reasons.extend(format_scan(corrects, wrongs))

    # ---- severity ----
    severity = _block_severity(reasons, dims, caric_max, inflated)

    # ---- человекочитаемость (независимая ось: text + sections всех опций) ----
    rdb = block_readability(opts)
    readability_severity = _readability_severity(rdb)

    correct_label = corrects[0][0].get("label") if len(corrects) == 1 else None
    lengths = {o.get("label") or f"#{o.get('order')}": m["len"] for o, m in metrics}
    return {
        "q_number": q_number, "block_idx": block_idx, "severity": severity,
        "reasons": reasons, "warnings": warnings,
        "correct_label": correct_label, "lengths": lengths,
        "caricature": [{"label": lbl, "groups": gs, "snippets": sn} for lbl, gs, sn in caric_detail],
        "readability": rdb, "readability_severity": readability_severity,
    }


def _block_severity(reasons, dims, caric_max, inflated):
    if not reasons:
        return "NONE"
    has_schema = any(r.startswith((
        "INVALID_CORRECT_COUNT",
        "MISSING_LABELS",
        "MISSING_OPTIONS",
        "INVALID_OPTION_COUNT",
        "INVALID_ORDER_SEQUENCE",
        "INVALID_LABEL_SEQUENCE",
        "LABEL_ORDER_MISMATCH",
    )) for r in reasons)
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
    rdb_issues = []
    nblocks = 0
    longest = shortest = most_tech = uniq_struct = 0
    rank_sum = 0
    for q in data.get("questions", []):
        if not isinstance(q, dict):
            continue
        for bi, b in enumerate(q.get("blocks", []) or []):
            raw_opts = b.get("options") if isinstance(b, dict) else None
            option_error = None if isinstance(raw_opts, list) else "MISSING_OPTIONS"
            opts = _norm_options(b) if isinstance(b, dict) else []
            nblocks += 1
            res = analyze_block(opts, q.get("q_number"), bi, option_error=option_error)
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
            if res.get("readability_severity", "NONE") != "NONE":
                rdb_issues.append(res)

    rate = longest / nblocks if nblocks else 0.0
    file_sev = "NONE"
    for it in issues:
        if SEVERITY_ORDER[it["severity"]] > SEVERITY_ORDER[file_sev]:
            file_sev = it["severity"]
    rdb_sev = "NONE"
    for it in rdb_issues:
        if SEVERITY_ORDER[it["readability_severity"]] > SEVERITY_ORDER[rdb_sev]:
            rdb_sev = it["readability_severity"]
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
        "readability_flags": len(rdb_issues),
        "readability_severity": rdb_sev,
        "readability_flag_rate": round(len(rdb_issues) / nblocks, 3) if nblocks else 0.0,
        "readability_issues": rdb_issues,
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


def analyze_all(paths, verbose_skip=False):
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
            if verbose_skip:
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
    for r in shown:
        mark = " ⚠LONGEST" if (r["blocks"] >= 6 and r["correct_longest_rate"] > 0.5) else ""
        print(f"{r['severity']:>8} {r['flags']:>5} {r['correct_longest_rate']:>4.0%} "
              f"{r['correct_most_technical_rate']:>4.0%}  {r['rel']}{mark}")
    total_blocks = sum(r["blocks"] for r in reports)
    total_flags = sum(r["flags"] for r in reports)
    counts = {
        s: sum(1 for r in rows if r["severity"] == s)
        for s in ("CRITICAL", "HIGH", "MEDIUM", "LOW")
    }
    print(f"\nфайлов: {len(reports)}; с флагами: {len(rows)}; блоков: {total_blocks}; флагов-блоков: {total_flags}")
    print(f"severity файлов — CRITICAL: {counts['CRITICAL']}, HIGH: {counts['HIGH']}, "
          f"MEDIUM: {counts['MEDIUM']}, LOW: {counts['LOW']}")
    if top and len(rows) > len(shown):
        print(f"показано: {len(shown)} из {len(rows)} файлов с флагами")
    print("Скрипт только находит кандидатов — финальное решение за человеком.")


def render_caricature(reports, verbose, top=None):
    rows = []
    for r in reports:
        car = [it for it in r["issues"] if it["caricature"]]
        if car:
            rows.append((r, car))
    rows.sort(key=lambda x: -len(x[1]))
    shown = rows[:top] if top else rows
    if verbose:
        for r, car in shown:
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
    for r, car in shown:
        total += len(car)
        print(f"{len(car):>6}  {r['rel']}")
    total_all = sum(len(car) for _, car in rows)
    print(f"\nфайлов с маркерами: {len(rows)}; блоков-кандидатов: {total_all}")
    if top and len(rows) > len(shown):
        print(f"показано: {len(shown)} из {len(rows)} файлов с маркерами")
    print("Скрипт только находит кандидатов — переписывание дистрактора за человеком.")


def render_readability(reports, verbose, top=None):
    rows = [r for r in reports if r.get("readability_flags")]
    rows.sort(key=lambda r: (-SEVERITY_ORDER[r.get("readability_severity", "NONE")], -r["readability_flags"]))
    shown = rows[:top] if top else rows
    if verbose:
        for r in shown:
            print(f"\n## {r['rel']}  blocks={r['blocks']}  readability={r['readability_severity']}  "
                  f"flag_rate={r['readability_flag_rate']:.0%}")
            for it in r["readability_issues"]:
                for x in it["readability"]:
                    print(f"   Q{it['q_number']} [{it['readability_severity']}] {x['code']} @{x['where']}: {x['detail']}")
    print("\n=== человекочитаемость русской прозы — кандидаты на правку ===")
    print(f"{'rdb-sev':>8} {'flags':>5} {'rate':>5}  file")
    code_tot = {}
    for r in reports:
        for it in r["readability_issues"]:
            for x in it["readability"]:
                code_tot[x["code"]] = code_tot.get(x["code"], 0) + 1
    for r in shown:
        print(f"{r['readability_severity']:>8} {r['readability_flags']:>5} "
              f"{r['readability_flag_rate']:>4.0%}  {r['rel']}")
    total_rdb = sum(r["readability_flags"] for r in reports)
    counts = {
        s: sum(1 for r in rows if r["readability_severity"] == s)
        for s in ("HIGH", "MEDIUM", "LOW")
    }
    print(f"\nфайлов с readability-флагами: {len(rows)}; блоков-кандидатов: {total_rdb}")
    print(f"readability severity файлов — HIGH: {counts['HIGH']}, MEDIUM: {counts['MEDIUM']}, LOW: {counts['LOW']}")
    if top and len(rows) > len(shown):
        print(f"показано: {len(shown)} из {len(rows)} файлов с readability-флагами")
    if code_tot:
        print("по сигналам: " + ", ".join(f"{k}={v}" for k, v in sorted(code_tot.items(), key=lambda x: -x[1])))
    print("Скрипт только находит кандидатов — переписывание прозы за человеком.")


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
            "readability_flags": r.get("readability_flags", 0),
            "readability_severity": r.get("readability_severity", "NONE"),
            "readability_flag_rate": r.get("readability_flag_rate", 0.0),
            "issues": [
                {
                    "q_number": it["q_number"], "block_idx": it["block_idx"],
                    "severity": it["severity"], "reasons": it["reasons"],
                    "options": {"correct_label": it["correct_label"], "lengths": it["lengths"]},
                    "caricature": it["caricature"],
                } for it in r["issues"]
            ],
            "readability_issues": [
                {
                    "q_number": it["q_number"], "block_idx": it["block_idx"],
                    "readability_severity": it["readability_severity"],
                    "readability": it["readability"],
                } for it in r.get("readability_issues", [])
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
            "readability_blocks_with_flags": sum(r.get("readability_flags", 0) for r in reports),
            "readability_severity_files": {
                s: sum(1 for r in reports if r.get("readability_severity") == s)
                for s in ("HIGH", "MEDIUM", "LOW")
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
           f"| Readability flagged blocks | {s.get('readability_blocks_with_flags', 0)} |",
           f"| Readability HIGH/MEDIUM/LOW files | "
           f"{s.get('readability_severity_files', {}).get('HIGH', 0)}/"
           f"{s.get('readability_severity_files', {}).get('MEDIUM', 0)}/"
           f"{s.get('readability_severity_files', {}).get('LOW', 0)} |",
           "", "## Top problematic files", "",
           "| Severity | Flags | Correct longest | Most technical | RdbSev | RdbFlags | File |",
           "|---|---:|---:|---:|:--:|---:|---|"]
    top = [f for f in rep["files"] if f["flags"]][:40]
    for f in top:
        out.append(f"| {f['severity']} | {f['flags']} | {f['correct_longest_rate']:.0%} | "
                   f"{f['correct_most_technical_rate']:.0%} | {f.get('readability_severity', 'NONE')} | "
                   f"{f.get('readability_flags', 0)} | `{f['path']}` |")
    # readability-first срез
    rdb_top = sorted(
        [f for f in rep["files"] if f.get("readability_flags")],
        key=lambda f: (-SEVERITY_ORDER[f.get("readability_severity", "NONE")], -f["readability_flags"]),
    )[:30]
    if rdb_top:
        out += ["", "## Top readability problems (русская проза)", "",
                "| RdbSev | Flags | Rate | File |", "|:--:|---:|---:|---|"]
        for f in rdb_top:
            out.append(f"| {f['readability_severity']} | {f['readability_flags']} | "
                       f"{f.get('readability_flag_rate', 0):.0%} | `{f['path']}` |")
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


def audit_file_readability(path):
    """Совместимый интерфейс: (flagged[(q_number, [(code, where, detail)])], nblocks, rdb_severity)."""
    rep = analyze_file(Path(path))
    if rep.get("skipped"):
        return [], 0, "NONE"
    flagged = []
    for it in rep.get("readability_issues", []):
        hits = [(x["code"], x["where"], x["detail"]) for x in it["readability"]]
        flagged.append((it["q_number"], hits))
    return flagged, rep["blocks"], rep.get("readability_severity", "NONE")


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

    # 6. человекочитаемость: непереведённый английский + вода + run-on
    r6 = blk([
        {"order": 0, "label": "A", "correct": True,
         "text": "Поток захватывает монитор и освобождает его сразу после критической секции."},
        {"order": 1, "label": "B", "correct": False,
         "text": "The thread always blocks on the monitor and never releases it until the whole batch is done."},
        {"order": 2, "label": "C", "correct": False,
         "text": "Важно отметить, что поток просто крутится в ожидании и не отдаёт процессор."},
        {"order": 3, "label": "D", "correct": False,
         "text": "Поток переходит в ожидание по таймеру и затем снова конкурирует за лок с другими потоками, "
                 "причём делает это многократно подряд и без какой-либо паузы между попытками, пока наконец "
                 "не получит доступ к разделяемому ресурсу, который ему совершенно необходим для дальнейшего "
                 "продолжения своей работы строго согласно заранее установленному в операционной системе общему "
                 "порядку приоритетного планирования всех готовых к исполнению потоков на доступных ядрах процессора."},
    ])
    codes6 = {x["code"] for x in r6["readability"]}
    assert "LOW_CYRILLIC" in codes6 or "ENGLISH_RUN" in codes6, r6["readability"]
    assert "RUS_READABILITY_CLICHE" in codes6, r6["readability"]
    assert "RUS_READABILITY_LONG_SENTENCE" in codes6, r6["readability"]
    assert r6["readability_severity"] in ("MEDIUM", "HIGH"), r6["readability_severity"]

    # 7. чистая русская проза с терминами в backticks → readability NONE (термины не палят язык)
    r7 = blk([
        {"order": 0, "label": "A", "correct": True,
         "text": "Используем `CompletableFuture.supplyAsync` с выделенным `ThreadPoolExecutor` для изоляции."},
        {"order": 1, "label": "B", "text": "Блокирующий вызов `get()` без таймаута на общем пуле.", "correct": False},
        {"order": 2, "label": "C", "text": "Синхронный вызов в цикле по списку задач без параллелизма.", "correct": False},
        {"order": 3, "label": "D", "text": "Запуск через `new Thread()` на каждую задачу без переиспользования.", "correct": False},
    ])
    assert r7["readability_severity"] == "NONE", (r7["readability_severity"], r7["readability"])

    # 8. Russian Readability Parity: машинная цепочка и перегруженная пунктуация.
    r8 = blk([
        {"order": 0, "label": "A", "correct": True,
         "text": "Ментор сначала уточняет цель разговора, затем выбирает один следующий шаг."},
        {"order": 1, "label": "B", "correct": False,
         "text": "Кандидат вспомнил контекст → оценил риск → озвучил срок → надавил на команду; "
                 "дальше добавил список: дедлайн; эскалация; контроль; отчёт."},
        {"order": 2, "label": "C", "text": "Разобрать кейс на ретроспективе и отделить процесс от персональной оценки.", "correct": False},
        {"order": 3, "label": "D", "text": "Согласовать ожидания один на один и проверить, есть ли общий контекст.", "correct": False},
    ])
    codes8 = {x["code"] for x in r8["readability"]}
    assert "RUS_READABILITY_TOO_MANY_ARROWS" in codes8, r8["readability"]
    assert "RUS_READABILITY_MACHINE_STYLE" in codes8, r8["readability"]
    assert "RUS_READABILITY_OVERSTRUCTURED" in codes8, r8["readability"]

    # 9. schema-like robustness: отсутствующие options не роняют анализ, а дают issue.
    r9 = analyze_block([], 1, 0, option_error="MISSING_OPTIONS")
    assert "MISSING_OPTIONS" in r9["reasons"], r9["reasons"]
    assert any(x.startswith("INVALID_OPTION_COUNT") for x in r9["reasons"]), r9["reasons"]

    # 10. schema-like robustness: missing label/order не должны приводить к TypeError.
    r10 = blk([
        {"text": "Нет label/order, но анализ должен вернуть issue.", "correct": True},
        {"order": 1, "label": "B", "text": "Обычный distractor.", "correct": False},
        {"order": 2, "label": "C", "text": "Обычный distractor.", "correct": False},
        {"order": 3, "label": "D", "text": "Обычный distractor.", "correct": False},
    ])
    assert "MISSING_LABELS" in r10["reasons"], r10["reasons"]
    assert any(x.startswith("INVALID_ORDER_SEQUENCE") for x in r10["reasons"]), r10["reasons"]

    print("selftest: OK (10 фикстур: length+struct+density, inflated-caricature, short-stub, "
          "invalid-correct, clean, readability-bad, readability-clean, rus-readability, missing-options, "
          "missing-label-order)")
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
    ap.add_argument("--readability", action="store_true", help="режим только человекочитаемости русской прозы")
    ap.add_argument("--json-report", metavar="PATH", help="сохранить машинный JSON-отчёт")
    ap.add_argument("--markdown-report", metavar="PATH", help="сохранить Markdown-отчёт")
    ap.add_argument("--fail-on", choices=["high", "critical"], help="exit 1 при наличии HIGH/CRITICAL")
    ap.add_argument("--verbose-skip", action="store_true", help="показывать пропущенные non-MCQ JSON")
    ap.add_argument("--selftest", action="store_true", help="прогнать встроенные фикстуры и выйти")
    args = ap.parse_args()

    if args.selftest:
        selftest()
        return 0

    reports = analyze_all(args.paths, verbose_skip=args.verbose_skip)

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
        render_caricature(reports, args.verbose, args.top)
    elif args.readability:
        render_readability(reports, args.verbose, args.top)
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
