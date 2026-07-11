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
FLAT_MIN_FRAC = 0.30       # дистрактор тоньше этой доли correct + без кода = flat

# --- TRUE "padded-clone" (hibernate) signature, а НЕ голый низкий CV. ---
# Урок 2026-07-11: низкий length-CV сам по себе НЕ дефект. terse-parallel файлы
# (короткие параллельные варианты-кандидаты, вся дидактика в sections — напр.
# jms-activemq, kotlin-testing, archunit) и естественно-варьирующие (vertx,
# loki-grafana) имеют низкий CV, но это ХОРОШИЙ дизайн. Настоящий дефект
# ROUND-6 — дистракторы, РАЗДУТЫЕ до клона ДЛИННОГО example-rich correct:
#   corLen велик  И  disMean/corLen ~1  И  все 4 опции одной длины (низкий CV).
STAMP_COR_MIN = 260        # correct длинный/богатый
STAMP_RATIO   = 0.82       # дистракторы раздуты почти до его длины
STAMP_CV_MAX  = 0.13       # и все 4 одной длины

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
    cvs, flats, nblk, corl, disl = [], 0, 0, [], []
    for q in d.get("questions", []):
        for b in q.get("blocks", []):
            opts = b.get("options", [])
            if len(opts) < 3:
                continue
            cor = [o for o in opts if o.get("correct")]
            if not cor:
                continue
            cvs.append(block_cv(opts))
            flats += block_flat(opts)
            corl.append(len(cor[0]["text"]))
            disl += [len(o["text"]) for o in opts if not o.get("correct")]
            nblk += 1
    if not cvs:
        return None
    cv = statistics.mean(cvs)
    corm = statistics.mean(corl)
    ratio = statistics.mean(disl) / corm if corm else 0
    stamped = corm > STAMP_COR_MIN and ratio > STAMP_RATIO and cv < STAMP_CV_MAX
    return round(cv, 3), nblk, flats, round(corm), round(ratio, 2), stamped

def cmd_rank(args):
    rows = []
    for f in glob.glob(ROOT + "/**/*-interview.json", recursive=True):
        st = file_stats(f)
        if st:
            stem = os.path.basename(f)[:-len("-interview.json")]
            rows.append((*st, stem))
    # очередь: только настоящие padded-clone, worst = самый длинный correct первым
    q = [r for r in rows if r[5]]
    q.sort(key=lambda r: -r[3])
    print("PADDED-CLONE queue (corLen>{} & disMean/corLen>{} & CV<{}) — worst=longest correct first"
          .format(STAMP_COR_MIN, STAMP_RATIO, STAMP_CV_MAX))
    print(f"{'CV':>6} {'blk':>4} {'corLen':>7} {'ratio':>6}  stem")
    for cv, n, fl, cm, ratio, _, s in q:
        print(f"{cv:>6} {n:>4} {cm:>7} {ratio:>6}  {s}  <<REDO")
    print(f"\nвсего файлов: {len(rows)}; genuine padded-clone: {len(q)} "
          f"(низкий CV сам по себе НЕ дефект — terse-parallel/varied файлы пропущены)")

def cmd_file(args):
    d = json.load(open(args.file))
    st = file_stats(args.file)
    verdict = "PADDED-CLONE — redo" if st and st[5] else "OK (not padded-clone)"
    print(f"{os.path.relpath(args.file)}  (GOLD-CV={GOLD_CV})  verdict: {verdict}")
    rows = []
    for q in d["questions"]:
        for b in q["blocks"]:
            opts = b["options"]
            if len(opts) < 3:
                continue
            rows.append((round(block_cv(opts), 3), block_flat(opts), q["q_number"],
                         [len(o["text"]) for o in opts]))
    for cv, fl, qn, L in sorted(rows):
        flatm = f" FLAT×{fl}" if fl else ""
        print(f"  Q{qn:>3}  CV={cv:>5}  lens={L}{flatm}")
    print(f"file: mean CV={st[0]}  corLen={st[3]}  disMean/corLen={st[4]}")

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
