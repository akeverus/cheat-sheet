#!/usr/bin/env python3
"""Строит матрицу перепроверки: строки = interview .md файлы и их вопросы,
столбцы = пункты работ A1..A5, B6, B7, C9. Детерминированные столбцы (A1-A5)
заполняются сразу; B6/B7/C9 = PENDING (заполняются модельными/app-проходами).

Запуск:  python3 scripts/readability/build_reverify_matrix.py
Выход:   scripts/readability/reverify_matrix.csv  (+ сводка в stdout)
"""
import json, glob, re, csv, collections, os

ROOT = os.getcwd()
MD_DIR = "cheatsheets/interview"
SEED_DIR = "modules/quiz-app/src/main/resources/seed/mcq"

CORRECT_SECTIONS = ["explanation", "example", "when_to_apply", "edge_cases", "related"]
WRONG_SECTIONS = ["what_actually", "how_it_should_be", "if_it_were_true", "source_of_confusion"]

# A1 «дубль-правильный дистрактор»: верный сигнал — секция if_it_were_true/what_actually
# ПРИЗНАЁТ ИСТИННЫМ САМ ТЕКСТ дистрактора (а не описывает правильное поведение в контраст).
# Контрфактическое «Если бы … тогда …» — это норма (распознаём и НЕ флагаем).
AFFIRM_RX = re.compile(r"(и есть (истинн|верн|корректн|правильн)|утверждение\s+(и есть\s+)?истинн|^\s*это верно[:\.])", re.I)
COUNTERFACT_RX = re.compile(r"\bесли бы\b", re.I)
FENCE_RX = re.compile(r"```.*?```", re.S)   # код-блоки исключаем из скана викилинков
# Только slug-образные цели (kebab-case): исключает массив-литералы [[1,2]], [[u, v, w]],
# [[Java,Kotlin]] и bash-тесты [[ -f ]], оставляя реальные Obsidian-викилинки.
LINK_RX = re.compile(r"\[\[([a-z][a-z0-9._/-]*)(?:#Q(\d+))?\]\]")

def affirms_self(secs):
    """True, если if_it_were_true (контрфактическое поле) вместо «если бы …»
    ПРИЗНАЁТ истинным сам текст неверного варианта — точный признак дубль-правильного
    дистрактора (как было в dns/distributed-systems). what_actually намеренно НЕ
    проверяем: там истинный факт-контраст — норма (ложные срабатывания на owasp/websocket)."""
    v = str(secs.get("if_it_were_true", ""))
    return bool(AFFIRM_RX.search(v)) and not COUNTERFACT_RX.search(v)

def rel_of_md(p):  return p[len(MD_DIR) + 1:-3]
def rel_of_seed(p): return p[len(SEED_DIR) + 1:-5]

# ---- collect files ----
md_files = sorted(p for p in glob.glob(f"{MD_DIR}/**/*.md", recursive=True)
                  if os.path.basename(p) not in ("TOC.md", "README.md"))
seed_files = sorted(glob.glob(f"{SEED_DIR}/**/*.json", recursive=True))

md_by_rel = {rel_of_md(p): p for p in md_files}
seed_by_rel = {rel_of_seed(p): p for p in seed_files}

# basename -> rel (for [[basename#Qn]] resolution); detect collisions
base_to_rels = collections.defaultdict(list)
for rel in set(md_by_rel) | set(seed_by_rel):
    base_to_rels[rel.split("/")[-1]].append(rel)

# md ## Q numbers per rel
HQ = re.compile(r"^##\s+Q(\d+)\b", re.M)
md_qnums = {}
md_text = {}
for rel, p in md_by_rel.items():
    t = open(p, encoding="utf-8").read()
    md_text[rel] = t
    md_qnums[rel] = sorted(int(m.group(1)) for m in HQ.finditer(t))

# seeder questions per rel
seed_q = {}     # rel -> {qnum: question_obj}
for rel, p in seed_by_rel.items():
    d = json.load(open(p, encoding="utf-8"))
    seed_q[rel] = {q["q_number"]: q for q in d["questions"]}

# valid Q targets per basename (union of md + seeder)
valid_q = collections.defaultdict(set)
for rel in set(md_qnums) | set(seed_q):
    valid_q[rel.split("/")[-1]] |= set(md_qnums.get(rel, []))
    valid_q[rel.split("/")[-1]] |= set(seed_q.get(rel, {}).keys())

# ---- per-row matrix ----
rows = []          # dict per row
findings = collections.Counter()

def add(rel, scope, **cols):
    r = {"file": rel, "scope": scope,
         "A1_dupcorrect": "", "A2_xlinks": "", "A3_parity": "", "A4_balance": "",
         "A5_sections": "", "B6_freshness": "PENDING", "B7_code": "PENDING",
         "C9_load": "PENDING", "notes": ""}
    r.update(cols)
    rows.append(r)
    return r

all_rels = sorted(set(md_by_rel) | set(seed_by_rel))

for rel in all_rels:
    base = rel.split("/")[-1]
    md_p = md_by_rel.get(rel)
    sd = seed_q.get(rel)
    notes = []

    # ---- A3 parity (FILE) ----
    mdqs = md_qnums.get(rel, [])
    if sd is None:
        a3 = "NO_SEEDER"
        findings["A3_no_seeder"] += 1
    elif md_p is None:
        a3 = "NO_MD"
        findings["A3_no_md"] += 1
    else:
        sqs = sorted(sd.keys())
        if mdqs == sqs:
            a3 = "PASS"
        else:
            a3 = f"MISMATCH md={len(mdqs)} json={len(sqs)}"
            findings["A3_mismatch"] += 1
            miss_md = set(sqs) - set(mdqs); miss_js = set(mdqs) - set(sqs)
            if miss_md: notes.append(f"json-only Q{sorted(miss_md)}")
            if miss_js: notes.append(f"md-only Q{sorted(miss_js)}")

    # ---- A4 balance (FILE) ----
    a4 = "N/A"
    if sd:
        pos = collections.Counter()
        for q in sd.values():
            for b in q["blocks"]:
                lab = [o["label"] for o in b["options"] if o["correct"]]
                if lab: pos[lab[0]] += 1
        tot = sum(pos.values())
        if tot:
            top = max(pos.values())
            a4 = "+".join(f"{k}{pos[k]}" for k in "ABCD")
            if top / tot > 0.45 and tot >= 6:
                a4 = "SKEW " + a4
                findings["A4_skew"] += 1

    # ---- A2 xlinks (FILE-level for md ## See also + inline) ----
    bad_links = []
    if md_p:
        scan_text = FENCE_RX.sub("", md_text[rel])   # без код-блоков (bash [[ ]] и т.п.)
        for m in LINK_RX.finditer(scan_text):
            tgt, qn = m.group(1).strip(), m.group(2)
            tb = tgt.split("/")[-1]
            if tb not in valid_q and tb not in base_to_rels:
                bad_links.append(f"{tgt}{'#Q'+qn if qn else ''}(no-file)")
            elif qn and int(qn) not in valid_q.get(tb, set()):
                bad_links.append(f"{tgt}#Q{qn}(no-q)")

    add(rel, "FILE",
        A1_dupcorrect="", A2_xlinks=("" if md_p is None else ("PASS" if not bad_links else f"BROKEN {len(bad_links)}")),
        A3_parity=a3, A4_balance=a4, A5_sections="",
        C9_load=("PENDING" if sd else "N/A"),
        notes="; ".join(notes + ([("md-bad-links: " + ", ".join(bad_links[:6])) ] if bad_links else [])))
    if bad_links: findings["A2_md_broken"] += len(bad_links)

    # ---- per-question rows ----
    if sd:
        for qn in sorted(sd):
            q = sd[qn]
            a1, a2, a5 = [], [], []
            for b in q["blocks"]:
                for o in b["options"]:
                    secs = o.get("sections", {})
                    # A5 section completeness
                    need = CORRECT_SECTIONS if o["correct"] else WRONG_SECTIONS
                    for k in need:
                        if not str(secs.get(k, "")).strip():
                            a5.append(f"{o['label']}:{k}-empty")
                    extra = set(secs) - set(need)
                    if extra: a5.append(f"{o['label']}:extra{sorted(extra)}")
                    # A1 self-admitting distractor (текст дистрактора признан истинным)
                    if not o["correct"] and affirms_self(secs):
                        a1.append(o["label"])
                    # A2 related links inside sections
                    for v in secs.values():
                        for m in LINK_RX.finditer(str(v)):
                            tgt, ql = m.group(1).strip(), m.group(2)
                            tb = tgt.split("/")[-1]
                            if tb not in valid_q and tb not in base_to_rels:
                                a2.append(f"{o['label']}:{tgt}(no-file)")
                            elif ql and int(ql) not in valid_q.get(tb, set()):
                                a2.append(f"{o['label']}:{tgt}#Q{ql}(no-q)")
            if a1: findings["A1_dupcorrect"] += len(a1)
            if a2: findings["A2_seed_broken"] += len(a2)
            if a5: findings["A5_section"] += len(a5)
            add(rel, f"Q{qn}",
                A1_dupcorrect=("DUP? " + ",".join(a1)) if a1 else "PASS",
                A2_xlinks=("BROKEN " + "; ".join(a2[:4])) if a2 else "PASS",
                A3_parity="", A4_balance="", A5_sections=("ISSUE " + "; ".join(a5[:4])) if a5 else "PASS",
                C9_load="")

# ---- write CSV ----
out = "scripts/readability/reverify_matrix.csv"
cols = ["file", "scope", "A1_dupcorrect", "A2_xlinks", "A3_parity", "A4_balance",
        "A5_sections", "B6_freshness", "B7_code", "C9_load", "notes"]
with open(out, "w", newline="", encoding="utf-8") as f:
    w = csv.DictWriter(f, fieldnames=cols)
    w.writeheader()
    w.writerows(rows)

print(f"files: md={len(md_files)} seeders={len(seed_files)}; matrix rows={len(rows)} -> {out}")
print("\n=== DETERMINISTIC FINDINGS (A1-A5) ===")
for k, v in sorted(findings.items()):
    print(f"  {k}: {v}")
# collisions
coll = {b: r for b, r in base_to_rels.items() if len(r) > 1}
if coll:
    print("\n  basename collisions:", coll)
