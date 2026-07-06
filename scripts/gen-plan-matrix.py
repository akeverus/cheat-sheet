#!/usr/bin/env python3
"""Генератор гигантской матрицы плана §7 PLAN_INTERVIEW.md.

Строки = все MCQ-сидеры. Столбцы = КАЖДАЯ конкретная проверка аудита
(не агрегаты, а индивидуальные сигналы). Ячейка = число блоков файла,
где эта проверка ещё срабатывает (0 = по этой оси уже поправлено).

Детерминированно, регенерируемо: источник — scripts/audit-mcq-parity.py.
Правит PLAN_INTERVIEW.md между маркерами <!-- MATRIX:START/END -->.

Запуск:
  python3 scripts/gen-plan-matrix.py            # печать в stdout
  python3 scripts/gen-plan-matrix.py --write     # вписать в PLAN_INTERVIEW.md
"""
import importlib.util, glob, sys, re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
spec = importlib.util.spec_from_file_location("aud", ROOT / "scripts/audit-mcq-parity.py")
aud = importlib.util.module_from_spec(spec); spec.loader.exec_module(aud)

SEED_GLOB = str(ROOT / "modules/quiz-app/src/main/resources/seed/mcq/**/*.json")

# --- гранулярные колонки: (ключ, заголовок, тип, детектор) ---------------------
# prefix-детектор по reasons; caric/readability считаются отдельно.
PARITY_COLS = [
    ("LEN_AVG",  "Lavg", "LEN_AVG"),
    ("LEN_SPD",  "Lspd", "LEN_SPREAD"),
    ("WORD",     "Word", "WORD_COUNT_GAP"),
    ("SENT",     "Sent", "SENTENCE_COUNT_GAP"),
    ("SHRT",     "Shrt", "SHORT_DISTR"),
    ("UNIQ",     "Uniq", "UNIQ_MARKER"),
    ("ENUM",     "Enum", "ONLY_ONE_ENUMERATION_OPTION"),
    ("CAUS",     "Caus", "ONLY_ONE_CAUSAL_OPTION"),
    ("SDEN",     "Sden", "STRUCTURE_DENSITY_GAP"),
    ("TECH",     "Tech", "TECH_DENSITY_GAP"),
    ("BTK",      "Btk",  "BACKTICK_GAP"),
    ("NUM",      "Num",  "NUMBER_GAP"),
    ("CMA",      "Cma",  "COMMA_GAP"),
    ("INF",      "Infl", "INFLATED_CARICATURE"),
]
CARIC_GROUPS = [
    ("Cabs", "absolute"),
    ("Ctox", "toxic_management"),
    ("Cabd", "absurd_action"),
    ("Cdis", "dismissive"),
    ("Cfak", "fake_reasoning"),
]
READ_CODES = [
    ("Rlong", "LONG_SENTENCE"),
    ("Rpun",  "PUNCT"),
    ("Rcyr",  "LOW_CYRILLIC"),
    ("Reng",  "ENGLISH_RUN"),
    ("Rfil",  "FILLER_PHRASE"),
]
SCHEMA_PREFIXES = ("INVALID_", "MISSING_LABELS", "LABEL_ORDER_MISMATCH")


def analyze(path):
    r = aud.analyze_file(Path(path))
    n_blocks = len(r["issues"])
    counts = {k: 0 for k, *_ in PARITY_COLS}
    counts.update({k: 0 for k, _ in CARIC_GROUPS})
    counts.update({k: 0 for k, _ in READ_CODES})
    counts["SCHEMA"] = 0
    car_total = 0
    for it in r["issues"]:
        reasons = it.get("reasons", [])
        for key, _hdr, pref in PARITY_COLS:
            if any(x.startswith(pref) for x in reasons):
                counts[key] += 1
        if any(any(x.startswith(p) for p in SCHEMA_PREFIXES) for x in reasons):
            counts["SCHEMA"] += 1
        # caricature groups
        block_groups = set()
        for c in it.get("caricature", []):
            for g in c.get("groups", []):
                block_groups.add(g)
        for key, gname in CARIC_GROUPS:
            if gname in block_groups:
                counts[key] += 1
        if block_groups or any(x.startswith("INFLATED") for x in reasons):
            car_total += 1
        # readability
        block_codes = set()
        for rd in it.get("readability", []):
            code = rd.get("code", "")
            code = code.replace("RUS_READABILITY_", "")
            block_codes.add(code)
        for key, cname in READ_CODES:
            if cname in block_codes:
                counts[key] += 1
    return {
        "rel": r["rel"], "blocks": n_blocks, "sev": r["severity"],
        "car": car_total, "counts": counts,
    }


def build_rows():
    rows = [analyze(f) for f in sorted(glob.glob(SEED_GLOB, recursive=True))]
    # worst-first: CAR desc, затем сумма parity-сигналов desc
    def parity_sum(r):
        return sum(r["counts"][k] for k, *_ in PARITY_COLS)
    rows.sort(key=lambda r: (r["car"], parity_sum(r), r["blocks"]), reverse=True)
    return rows


def cell(n):
    return str(n) if n else "·"


def topic(rel):
    return Path(rel).stem.replace("-interview", "")


def render(rows):
    hdrs = ["#", "Сидер", "Бл", "Sev", "ΣCAR"]
    hdrs += [h for _, h, _ in PARITY_COLS]
    hdrs += [k for k, _ in CARIC_GROUPS]
    hdrs += [k for k, _ in READ_CODES]
    hdrs += ["Sch", "Статус"]
    align = ["--:", "---", "--:", ":--:", "--:"] + ["--:"] * (len(PARITY_COLS)+len(CARIC_GROUPS)+len(READ_CODES)) + ["--:", ":--:"]
    out = ["| " + " | ".join(hdrs) + " |", "|" + "|".join(align) + "|"]
    for i, r in enumerate(rows, 1):
        c = r["counts"]
        status = "✅CAR0" if r["car"] == 0 else f"CAR{r['car']}"
        cells = [str(i), topic(r["rel"]), str(r["blocks"]), r["sev"].replace("CRITICAL","CRIT").replace("MEDIUM","MED"), cell(r["car"])]
        cells += [cell(c[k]) for k, *_ in PARITY_COLS]
        cells += [cell(c[k]) for k, _ in CARIC_GROUPS]
        cells += [cell(c[k]) for k, _ in READ_CODES]
        cells += [cell(c["SCHEMA"]), status]
        out.append("| " + " | ".join(cells) + " |")
    return "\n".join(out)


def summary(rows):
    n = len(rows)
    car0 = sum(1 for r in rows if r["car"] == 0)
    car_blocks = sum(r["car"] for r in rows)
    return (f"файлов={n} · caricature-clean(CAR0)={car0}/{n} · остаток CAR-блоков={car_blocks} · "
            f"строки worst-first (ΣCAR↓, затем Σparity↓). Ячейка=число блоков, где проверка ещё срабатывает; "
            f"`·`=0 (по этой оси чисто).")


LEGEND = """**Как читать (каждая колонка = отдельная проверка аудита; ячейка = сколько блоков ещё править, `·`=0):**

| Колонка | Проверка (что править) |
|---|---|
| **Бл** | всего MCQ-блоков в файле |
| **Sev** | severity файла (худший блок) |
| **ΣCAR** | всего блоков с карикатурой — главный таргет цикла (loop чинит именно это) |
| **Lavg** | correct/avg-wrong length ratio (correct длиннее в среднем) |
| **Lspd** | max/min длина опций (разброс) |
| **Word** | WORD_COUNT_GAP (correct больше слов) |
| **Sent** | SENTENCE_COUNT_GAP (correct больше предложений) |
| **Shrt** | SHORT_DISTR (есть дистрактор-заглушка < порога) |
| **Uniq** | UNIQ_MARKER (только correct несёт →/;/backtick-enum) |
| **Enum** | ONLY_ONE_ENUMERATION_OPTION (только correct — перечисление) |
| **Caus** | ONLY_ONE_CAUSAL_OPTION (только один вариант — причинно-следств.) |
| **Sden** | STRUCTURE_DENSITY_GAP (структурная плотность correct выше) |
| **Tech** | TECH_DENSITY_GAP (технических терминов у correct больше) |
| **Btk** | BACKTICK_GAP (backtick'ов у correct больше) |
| **Num** | NUMBER_GAP (чисел у correct больше) |
| **Cma** | COMMA_GAP (запятых/смысловых частей у correct больше) |
| **Infl** | INFLATED_CARICATURE (дистрактор раздут до формы correct, но карикатурен) |
| **Cabs** | карикатура: категоричность (всегда/никогда/…) |
| **Ctox** | карикатура: токсичный менеджмент (заставить/угрожать/…) |
| **Cabd** | карикатура: абсурдное действие (просто игнорировать/забить/…) |
| **Cdis** | карикатура: обесценивание (без амбиций/оставить в покое/…) |
| **Cfak** | карикатура: псевдо-обоснование (якобы/очевидно же/…) |
| **Rlong** | readability: слишком длинное предложение |
| **Rpun** | readability: склейка пунктуации |
| **Rcyr** | readability: мало кириллицы (непереведённая англ. проза) |
| **Reng** | readability: длинный английский run |
| **Rfil** | readability: вода/filler-фраза |
| **Sch** | schema-ошибки (INVALID_*/MISSING/LABEL_ORDER) — обязано быть 0 |
| **Статус** | `✅CAR0` = карикатура вычищена (round-3 done) · `CAR<N>` = остаток в очереди |

> **Приоритет цикла:** loop чинит **ΣCAR/Cxxx-колонки** (Plausibility). Length/structure-колонки (Lavg…Cma)
> у длинных энумеративных correct снимаются НЕ обрезкой correct (запрещено), а поднятием дистракторов —
> это делается попутно в тех же блоках. Readability (Rxxx) — независимая ось. Матрица регенерируется
> каждый тик: `python3 scripts/gen-plan-matrix.py --write`."""


def main():
    rows = build_rows()
    block = (f"<!-- MATRIX:START (auto: scripts/gen-plan-matrix.py) -->\n\n"
             f"_Сводка: {summary(rows)}_\n\n"
             f"{LEGEND}\n\n"
             f"{render(rows)}\n\n"
             f"<!-- MATRIX:END -->")
    if "--write" in sys.argv:
        plan = ROOT / "PLAN_INTERVIEW.md"
        text = plan.read_text(encoding="utf-8")
        if "<!-- MATRIX:START" in text and "<!-- MATRIX:END -->" in text:
            text = re.sub(r"<!-- MATRIX:START.*?<!-- MATRIX:END -->", block, text, flags=re.S)
        else:
            # первая генерация: заменить старую таблицу §7 (от строки-заголовка до ## 8)
            m = re.search(r"\n\| # \| Кат\..*?(?=\n## 8\.)", text, flags=re.S)
            if m:
                text = text[:m.start()] + "\n" + block + "\n\n" + text[m.end():]
            else:
                raise SystemExit("не нашёл ни маркеров, ни старой таблицы §7")
        if not text.endswith("\n"):
            text += "\n"
        plan.write_text(text, encoding="utf-8")
        print(f"wrote matrix into PLAN_INTERVIEW.md: {len(rows)} rows")
    else:
        print(block)


if __name__ == "__main__":
    main()
