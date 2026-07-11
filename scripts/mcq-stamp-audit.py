#!/usr/bin/env python3
"""MCQ "stamped clone" audit — обратная сторона structure-parity.

Проблема (пользователь, 2026-07-11): гонка structure-tell -> 0 привела к тому,
что дистракторы стали КАРБОН-КОПИЯМИ correct — одинаковая длина, пунктуация,
форма. Это тоже палево и просто плохо читается. Эталон качества — `rxjava`
(length-CV=0.195) и `java-functional-interface` (0.204): дистракторы substantive
и код-насыщенные, но с ЕСТЕСТВЕННЫМ разбросом длины/формы.

Метрика: length-CV блока = stdev(len 4 опций)/mean(len). Низкий CV = отштамповано.
Файл считается "stamped" (в очередь на gold-style redo), когда средний по блокам
CV < STAMP_MAX. Здоровый диапазон — как у корпуса (медиана ~0.46) и не ниже gold.

Режимы:
  python3 scripts/mcq-stamp-audit.py --rank [--top N]   очередь worst-first (низкий CV)
  python3 scripts/mcq-stamp-audit.py --file <path>       CV по блокам одного файла
"""
import json, glob, os, statistics, argparse

ROOT = "modules/quiz-app/src/main/resources/seed/mcq"
GOLD_CV = 0.195            # rxjava — нижняя планка "естественности"
STAMP_MAX = 0.14           # файл ниже этого считается отштампованным (в очередь)
FLAT_MIN_FRAC = 0.30       # дистрактор тоньше этой доли correct + без кода = flat

def block_cv(opts):
    L = [len(o["text"]) for o in opts]
    m = statistics.mean(L)
    return statistics.pstdev(L) / m if m else 0.0

def block_flat(opts):
    cor = [o for o in opts if o.get("correct")]
    if not cor:
        return 0
    cl = len(cor[0]["text"])
    flat = 0
    for o in opts:
        if o.get("correct"):
            continue
        if o["text"].count("`") // 2 == 0 and len(o["text"]) < FLAT_MIN_FRAC * cl:
            flat += 1
    return flat

def file_stats(path):
    d = json.load(open(path))
    cvs, flats, nblk = [], 0, 0
    for q in d.get("questions", []):
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            if len(opts) < 3:
                continue
            cvs.append(block_cv(opts))
            flats += block_flat(opts)
            nblk += 1
    if not cvs:
        return None
    return round(statistics.mean(cvs), 3), nblk, flats

def cmd_rank(args):
    rows = []
    for f in glob.glob(ROOT + "/**/*-interview.json", recursive=True):
        st = file_stats(f)
        if st:
            stem = os.path.basename(f)[:-len("-interview.json")]
            rows.append((st[0], st[1], st[2], stem))
    rows.sort()
    print(f"GOLD rxjava length-CV={GOLD_CV} | STAMP_MAX={STAMP_MAX} (ниже = в очередь на gold-style redo)")
    print(f"{'CV':>6} {'blk':>4} {'flat':>4}  stem")
    top = args.top or len(rows)
    stamped = 0
    for cv, n, fl, s in rows[:top]:
        mark = "  <<STAMPED" if cv < STAMP_MAX else ""
        if cv < STAMP_MAX:
            stamped += 1
        print(f"{cv:>6} {n:>4} {fl:>4}  {s}{mark}")
    tot_stamped = sum(1 for r in rows if r[0] < STAMP_MAX)
    print(f"\nвсего файлов: {len(rows)}; отштамповано (CV<{STAMP_MAX}): {tot_stamped}")

def cmd_file(args):
    d = json.load(open(args.file))
    print(f"{os.path.relpath(args.file)}  (GOLD-CV={GOLD_CV}, STAMP_MAX={STAMP_MAX})")
    rows = []
    for q in d["questions"]:
        for b in q["blocks"]:
            opts = b["options"]
            if len(opts) < 3:
                continue
            rows.append((round(block_cv(opts), 3), block_flat(opts), q["q_number"],
                         [len(o["text"]) for o in opts]))
    for cv, fl, qn, L in sorted(rows):
        mark = "  STAMPED" if cv < STAMP_MAX else ""
        flatm = f" FLAT×{fl}" if fl else ""
        print(f"  Q{qn:>3}  CV={cv:>5}  lens={L}{mark}{flatm}")
    print(f"file mean CV = {round(statistics.mean(r[0] for r in rows),3)}")

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--rank", action="store_true")
    ap.add_argument("--file")
    ap.add_argument("--top", type=int)
    a = ap.parse_args()
    if a.file:
        cmd_file(a)
    else:
        cmd_rank(a)

if __name__ == "__main__":
    main()
