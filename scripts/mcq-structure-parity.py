#!/usr/bin/env python3
"""MCQ Structure/Format Parity detector, ranker и gate.

Проблема (закреплена пользователем 2026-07-07): правильный вариант часто имеет
БОГАТУЮ структуру (перечисление на несколько частей, скобочные уточнения,
код-спаны, несколько предложений), а дистракторы — плоскую одно-клаузную прозу.
Эта структурная асимметрия — палево: correct угадывается по ФОРМЕ, без знания темы.

Требование: у ВСЕХ опций блока (correct и дистракторы) одинаковая СТРУКТУРА и
ФОРМАТ — то же число предложений/частей перечисления, те же скобочные уточнения,
те же код-спаны, тот же коннективный паттерн. Отличие — только ОДНА смысловая
ошибка в одном элементе.

structure-tell (на блок) = сумма |feat(correct) - mean(feat(distractors))| по
структурным признакам. GOLD rxjava ≈ 10.5 — это планка. Файлы, где correct
структурно выделяется, дают 18-25+.

Режимы:
  python3 scripts/mcq-structure-parity.py --rank [--exclude a,b] [--top N]
      ранжирование всех seed-файлов worst-first по среднему structure-tell.
  python3 scripts/mcq-structure-parity.py --file <path> [--verbose]
      per-block structure-tell для одного файла (для гейта/ревью).
  python3 scripts/mcq-structure-parity.py --block <path> <q_number>
      детальный разбор одного блока (features по опциям).
"""
import json, re, glob, os, sys, statistics, argparse

ROOT = "modules/quiz-app/src/main/resources/seed/mcq"
GOLD = ROOT + "/reactive/rxjava-interview.json"

# структурные признаки текста опции (БЕЗ голой длины — она покрыта length-parity)
def features(t):
    return {
        "sent":  t.count(". ") + 1,          # число предложений
        "semi":  t.count(";"),               # перечисление через ;
        "colon": t.count(":"),               # ввод списка/пояснения
        "paren": t.count("("),               # скобочные уточнения
        "code":  t.count("`") // 2,          # код-спаны
        "enum":  len(re.findall(r"\b\d\)", t)),  # нумерация 1) 2) 3)
        "dash":  t.count("—") + t.count(" - "),  # тире-перечисление/пояснение
        "comma": t.count(","),               # плотность придаточных/списков
    }

FEATS = ["sent", "semi", "colon", "paren", "code", "enum", "dash", "comma"]

def block_tell(b):
    cor = [o for o in b["options"] if o.get("correct")]
    dis = [o for o in b["options"] if not o.get("correct")]
    if len(cor) != 1 or not dis:
        return None, None
    c = features(cor[0]["text"])
    ds = [features(o["text"]) for o in dis]
    score = 0.0
    detail = {}
    for k in FEATS:
        dm = statistics.mean(d[k] for d in ds)
        score += abs(c[k] - dm)
        detail[k] = (c[k], round(dm, 1))
    # directional: насколько correct СТРУКТУРНО БОГАЧЕ дистракторов (главный палёж)
    over = sum(max(0.0, c[k] - statistics.mean(d[k] for d in ds)) for k in FEATS)
    return round(score, 2), {"detail": detail, "over": round(over, 2)}

def file_scores(path):
    data = json.load(open(path))
    rows = []
    for q in data.get("questions", []):
        for b in q.get("blocks", []):
            s, extra = block_tell(b)
            if s is not None:
                rows.append((s, q["q_number"], extra))
    return rows

def file_mean(path):
    rows = file_scores(path)
    if not rows:
        return None
    return round(statistics.mean(r[0] for r in rows), 2), len(rows)

def gold_baseline():
    m = file_mean(GOLD)
    return m[0] if m else None

def cmd_rank(args):
    exclude = set((args.exclude or "").split(",")) if args.exclude else set()
    base = gold_baseline()
    rows = []
    for f in glob.glob(ROOT + "/**/*-interview.json", recursive=True):
        stem = os.path.basename(f)[:-len("-interview.json")]
        if stem in exclude or f == GOLD:
            continue
        m = file_mean(f)
        if m:
            rows.append((m[0], m[1], stem, os.path.relpath(f)))
    rows.sort(reverse=True)
    print(f"GOLD rxjava structure-tell baseline = {base}  (цель: подтянуть файлы к этому уровню)")
    print(f"{'tell':>6} {'blk':>4}  stem")
    top = args.top or len(rows)
    for r in rows[:top]:
        flag = "  <<WORST" if r[0] >= (base or 11) * 1.4 else ""
        print(f"{r[0]:>6} {r[1]:>4}  {r[2]}{flag}")
    print(f"\nвсего файлов: {len(rows)}; выше 1.4×gold ({round((base or 11)*1.4,1)}): "
          f"{sum(1 for r in rows if r[0] >= (base or 11)*1.4)}")

def cmd_file(args):
    base = gold_baseline()
    rows = file_scores(args.file)
    if not rows:
        print("no scorable blocks"); return
    mean = round(statistics.mean(r[0] for r in rows), 2)
    print(f"{os.path.relpath(args.file)}  mean structure-tell = {mean}  (gold={base})")
    for s, qn, extra in sorted(rows, reverse=True):
        mark = "  OVER" if extra["over"] >= 6 else ""
        print(f"  Q{qn:>3}  tell={s:>5}  over={extra['over']:>4}{mark}")
        if args.verbose:
            print(f"        {extra['detail']}")

def cmd_block(args):
    data = json.load(open(args.block[0]))
    qn = int(args.block[1])
    for q in data["questions"]:
        for b in q["blocks"]:
            if q["q_number"] == qn:
                s, extra = block_tell(b)
                print(f"Q{qn} structure-tell={s} over={extra['over']}")
                print(f"features per option ({FEATS}):")
                for o in b["options"]:
                    f = features(o["text"])
                    tag = "✓COR" if o.get("correct") else " dis"
                    print(f"  [{o['label']}]{tag} " + " ".join(f"{k}={f[k]}" for k in FEATS)
                          + f"  len={len(o['text'])}")
                return
    print("block not found")

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--rank", action="store_true")
    ap.add_argument("--file")
    ap.add_argument("--block", nargs=2)
    ap.add_argument("--exclude")
    ap.add_argument("--top", type=int)
    ap.add_argument("--verbose", action="store_true")
    args = ap.parse_args()
    if args.rank:
        cmd_rank(args)
    elif args.file:
        cmd_file(args)
    elif args.block:
        cmd_block(args)
    else:
        ap.print_help()

if __name__ == "__main__":
    main()
