#!/usr/bin/env python3
"""Generate the ROUND-6 STRUCTURE/FORMAT PARITY plan — a per-file table, worst-first by
mean structure-tell. Deterministic: regenerate every tick as files get reworked so the
table always reflects the live corpus. Writes docs/mcq-structure-parity-plan.md.

structure-tell(block) = Σ |feat(correct) − mean(feat(distractors))| over 8 structural
features {sent, semi, colon, paren, code, enum, dash, comma}. GOLD rxjava ≈ 9.09.
A file is "parity" (✅) once no block exceeds PARITY_MAX. Mean tell remains a
ranking signal only and cannot hide local structure tells.

Usage: python3 scripts/gen-structure-parity-plan.py [--write]
"""
import json, re, glob, os, sys, statistics

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SEED = ROOT + "/modules/quiz-app/src/main/resources/seed/mcq"
GOLD_STEM = "rxjava"
OUT = ROOT + "/docs/mcq-structure-parity-plan.md"
PARITY_MAX = 10.0   # maximum allowed structure-tell for every block
# parallel-session / dirty — do not touch; shown as ⏸ in the plan
OFFLIMITS = {"agentic-patterns", "git", "chaos-engineering", "postgresql", "testcontainers", "mentoring"}

def features(t):
    return {"sent":t.count(". ")+1,"semi":t.count(";"),"colon":t.count(":"),"paren":t.count("("),
            "code":t.count("`")//2,"enum":len(re.findall(r"\b\d\)",t)),"dash":t.count("—")+t.count(" - "),"comma":t.count(",")}
FEATS=["sent","semi","colon","paren","code","enum","dash","comma"]
def block_tell(b):
    c=[o for o in b["options"] if o.get("correct")]; d=[o for o in b["options"] if not o.get("correct")]
    if len(c)!=1 or not d: return None
    cf=features(c[0]["text"]); ds=[features(o["text"]) for o in d]
    return sum(abs(cf[k]-statistics.mean(x[k] for x in ds)) for k in FEATS)

def file_stats(path):
    data=json.load(open(path))
    tells=[]
    for q in data.get("questions",[]):
        for b in q.get("blocks",[]):
            t=block_tell(b)
            if t is not None: tells.append(t)
    if not tells: return None
    return {"mean":round(statistics.mean(tells),1),"blocks":len(tells),
            "over":sum(1 for t in tells if t>PARITY_MAX)}

def category(path):
    rel=os.path.relpath(path, SEED)
    return rel.split(os.sep)[0]

def main():
    rows=[]
    gold=None
    for f in glob.glob(SEED+"/**/*-interview.json", recursive=True):
        stem=os.path.basename(f)[:-len("-interview.json")]
        st=file_stats(f)
        if not st: continue
        if stem==GOLD_STEM: gold=st; continue
        rows.append({"stem":stem,"cat":category(f),**st})
    rows.sort(key=lambda r:(-r["mean"], -r["over"]))

    total=len(rows)
    done=[r for r in rows if r["over"]==0 and r["stem"] not in OFFLIMITS]
    review=[r for r in rows if r["mean"]<=PARITY_MAX and r["over"]>0 and r["stem"] not in OFFLIMITS]
    queued=[r for r in rows if r["mean"]>PARITY_MAX and r["stem"] not in OFFLIMITS]
    offl=[r for r in rows if r["stem"] in OFFLIMITS]

    L=[]
    L.append("# ROUND-6 — STRUCTURE / FORMAT PARITY: план по каждому JSON-файлу")
    L.append("")
    L.append("> Автогенерация `scripts/gen-structure-parity-plan.py --write` (детерминированно из корпуса, регенерируется каждый тик). НЕ править вручную.")
    L.append("")
    L.append("**Правило:** у всех 4 опций блока одна структура и формат — дистрактор клонирует скелет correct (перечисление, скобки, код-спаны, стрелочная цепочка) с ровно одной смысловой подменой. Эталон-образец: `algorithms-interview.json` (см. commit).")
    L.append("")
    L.append(f"**Метрика:** block-level structure-tell; mean используется только для worst-first сортировки. GOLD `rxjava` = {gold['mean'] if gold else '?'}. Файл считается на паритете, когда блоков >{PARITY_MAX:.0f} не осталось.")
    L.append("")
    L.append(f"**Прогресс:** ✅ {len(done)} на паритете · ⬜ {len(queued)} в очереди · 🟡 {len(review)} перепроверить · ⏸ {len(offl)} off-limits (параллельная сессия) · всего {total}.")
    if queued:
        avg_q=round(statistics.mean(r["mean"] for r in queued),1)
        L.append(f"Средний tell в очереди = {avg_q}; худший = {queued[0]['stem']} ({queued[0]['mean']}).")
    L.append("")
    L.append("| # | Статус | Файл | Категория | Блоков | tell | Блоков >10 |")
    L.append("|--:|:--:|:--|:--|--:|--:|--:|")
    for i,r in enumerate(rows,1):
        if r["stem"] in OFFLIMITS: status="⏸"
        elif r["over"]==0: status="✅"
        elif r["mean"]<=PARITY_MAX: status="🟡"
        else: status="⬜"
        L.append(f"| {i} | {status} | `{r['stem']}` | {r['cat']} | {r['blocks']} | {r['mean']} | {r['over']} |")
    L.append("")
    md="\n".join(L)+"\n"

    if "--write" in sys.argv:
        os.makedirs(os.path.dirname(OUT), exist_ok=True)
        open(OUT,"w").write(md)
        print(f"wrote {OUT}: {total} files, ✅{len(done)} ⬜{len(queued)} 🟡{len(review)} ⏸{len(offl)}")
    else:
        print(md[:1500])
        print(f"...\n[dry-run] {total} files, ✅{len(done)} ⬜{len(queued)} 🟡{len(review)} ⏸{len(offl)}. Add --write.")

if __name__=="__main__": main()
