#!/usr/bin/env python3
"""Генератор живой таблицы PLAN_INTERVIEW §7 — проверка каждого MCQ-сидера против
docs/golden-examples.md (Visual Format / Plausibility / Readability parity).

Один ряд = interview-сидер. Колонка **Golden** = статус проверки файла против
`docs/golden-examples.md`:
  ⬜  QUEUED   — ещё не приведён к golden-стилю
  🔄  WIP      — в работе
  ✅  DONE     — проверен и приведён к golden-стилю (correct не угадать глазами)

Колонка **Tell** = сколько блоков файла ещё визуально «палят» correct (детермини-
рованный проксей: correct — самый длинный / единственный с двоеточием / несёт
заметно больше backtick'ов / единственный с перечислением). Это worst-first ключ.
Tell — диагностика, НЕ авто-гейт: DONE ставит редактор после golden self-review.

Статусы СОХРАНЯЮТСЯ между регенерациями (парсятся из текущей таблицы по имени
файла). Флаг --reset обнуляет все статусы в ⬜ (новый прогон по всему корпусу).

  python3 scripts/gen-interview-plan.py --write           # регенерировать, сохранив статусы
  python3 scripts/gen-interview-plan.py --write --reset    # + сбросить все статусы в ⬜
"""
import json, glob, os, re, argparse

ROOT = "modules/quiz-app/src/main/resources/seed/mcq"
PLAN = "PLAN_INTERVIEW.md"
START = "<!-- INTERVIEW-PLAN:START (auto: scripts/gen-interview-plan.py) -->"
END = "<!-- INTERVIEW-PLAN:END -->"


def btk(s):
    return s.count("`") // 2


def block_tell(opts):
    """1, если correct визуально отличим от дистракторов хотя бы по одной оси."""
    cor = [o for o in opts if o.get("correct")]
    if not cor:
        return 0
    c = cor[0]["text"]
    wrong = [o["text"] for o in opts if not o.get("correct")]
    if not wrong:
        return 0
    cl = len(c)
    # 1) correct строго самый длинный
    if all(cl > len(w) for w in wrong):
        return 1
    # 2) correct — единственный с двоеточием
    if ":" in c and all(":" not in w for w in wrong):
        return 1
    # 3) correct несёт заметно больше backtick-идентификаторов
    cb = btk(c)
    if cb >= 2 and cb > max((btk(w) for w in wrong), default=0) + 1:
        return 1
    # 4) correct — единственный с перечислением (`;` или нумерация «1)»/«— »)
    def has_enum(t):
        return (";" in t) or bool(re.search(r"\d\)", t)) or (" — " in t and t.count(" — ") >= 2)
    if has_enum(c) and not any(has_enum(w) for w in wrong):
        return 1
    return 0


def file_stats(path):
    d = json.load(open(path, encoding="utf-8"))
    nblk = tell = 0
    for q in d.get("questions", []):
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            if len(opts) < 3 or not any(o.get("correct") for o in opts):
                continue
            nblk += 1
            tell += block_tell(opts)
    return nblk, tell


def load_existing_status():
    """Считать текущие статусы из таблицы (по имени файла), чтобы не терять прогресс."""
    status = {}
    if not os.path.exists(PLAN):
        return status
    txt = open(PLAN, encoding="utf-8").read()
    for line in txt.splitlines():
        m = re.match(r"^\|\s*\d+\s*\|\s*`([^`]+)`\s*\|.*\|\s*(⬜|🔄|✅)\s*\|\s*$", line)
        if m:
            status[m.group(1)] = m.group(2)
    return status


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--write", action="store_true")
    ap.add_argument("--reset", action="store_true")
    a = ap.parse_args()

    prev = {} if a.reset else load_existing_status()
    rows = []
    for f in sorted(glob.glob(ROOT + "/**/*-interview.json", recursive=True)):
        stem = os.path.basename(f)[: -len("-interview.json")]
        cat = os.path.basename(os.path.dirname(f))
        nblk, tell = file_stats(f)
        rows.append((stem, cat, nblk, tell, prev.get(stem, "⬜")))

    # worst-first: больше палящих блоков → выше; затем крупнее файл
    rows.sort(key=lambda r: (-r[3], -r[2], r[0]))

    done = sum(1 for r in rows if r[4] == "✅")
    wip = sum(1 for r in rows if r[4] == "🔄")
    queued = len(rows) - done - wip
    tell_left = sum(r[3] for r in rows if r[4] != "✅")

    out = [START, ""]
    out.append(
        f"_Сводка: сидеров={len(rows)} · ✅DONE={done} · 🔄WIP={wip} · ⬜QUEUED={queued} · "
        f"остаток визуально-палящих блоков (по не-DONE)={tell_left}. "
        f"Проверка каждого файла — против `docs/golden-examples.md`._"
    )
    out.append("")
    out.append(
        "**Golden** = статус проверки файла против `docs/golden-examples.md` "
        "(⬜ QUEUED · 🔄 WIP · ✅ DONE). **Tell** = сколько блоков ещё визуально палят "
        "correct (самый длинный / единств. с `:` / больше backtick'ов / единств. с "
        "перечислением) — worst-first ключ, диагностика, не авто-гейт."
    )
    out.append("")
    out.append("| # | Сидер | Категория | Бл | Tell | Golden |")
    out.append("|--:|---|---|--:|--:|:--:|")
    for i, (stem, cat, nblk, tell, st) in enumerate(rows, 1):
        tcell = str(tell) if tell else "·"
        out.append(f"| {i} | `{stem}` | {cat} | {nblk} | {tcell} | {st} |")
    out.append("")
    out.append(END)
    block = "\n".join(out)

    if a.write:
        txt = open(PLAN, encoding="utf-8").read()
        if START in txt and END in txt:
            txt = re.sub(re.escape(START) + r".*?" + re.escape(END), block, txt, flags=re.S)
        else:
            txt = txt.rstrip() + "\n\n" + block + "\n"
        open(PLAN, "w", encoding="utf-8").write(txt)
        print(f"WROTE {PLAN}: {len(rows)} сидеров, ✅{done}/🔄{wip}/⬜{queued}, tell-left={tell_left}")
    else:
        print(block[:2000])
        print(f"\n... {len(rows)} сидеров, ✅{done}/🔄{wip}/⬜{queued}, tell-left={tell_left}")


if __name__ == "__main__":
    main()
