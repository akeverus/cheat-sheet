#!/usr/bin/env python3
"""Восстановление mermaid-диаграмм в interview .md (revert LANE 8A convert→прозой).

Подход: для каждого файла находим его собственный коммит-удаления mermaid C
(pickaxe по ```mermaid), затем git 3-way merge:
  base   = C    (после удаления: проза-замена, 0 mermaid)
  other  = C^   (до удаления: mermaid на месте)
  current= рабочее дерево (проза-замена + поздние правки)
Результат = current + (base->other) = поздние правки СОХРАНЕНЫ + mermaid восстановлен,
конфликты только там, где поздние правки пересекают регион замены.

Применяет (--apply) только при чистом merge и совпадении кол-ва восстановленных
mermaid с C^. Конфликты/несовпадения — в лог для ручной обработки.
"""
import subprocess, sys, os, tempfile, pathlib, json

ROOT = "/Users/sergeyvoronin/IdeaProjects/cheat-sheet"
APPLY = "--apply" in sys.argv
LIMIT = None
for a in sys.argv:
    if a.startswith("--limit="):
        LIMIT = int(a.split("=", 1)[1])
ONLY = [a.split("=",1)[1] for a in sys.argv if a.startswith("--only=")]

def git(*args, capture=True, text=True):
    return subprocess.run(["git", *args], cwd=ROOT, capture_output=capture, text=text)

def git_out(*args):
    r = git(*args)
    return r.stdout if r.returncode == 0 else None

def show(rev_path):
    r = subprocess.run(["git", "show", rev_path], cwd=ROOT, capture_output=True)
    return r.stdout if r.returncode == 0 else None  # bytes

def count_mermaid_bytes(b):
    return b.count(b"```mermaid") if b else 0

def dirty_set():
    out = git_out("status", "--short", "cheatsheets/interview") or ""
    s = set()
    for line in out.splitlines():
        p = line[3:].strip()
        if p:
            s.add(p)
    return s

def removal_commit(f):
    """Самый свежий коммит, изменивший число ```mermaid в файле (= удаление)."""
    out = git_out("log", "--format=%H", "-S", "```mermaid", "--", f)
    if not out:
        return None
    for h in out.splitlines():
        h = h.strip()
        if not h:
            continue
        # после h в текущем дереве mermaid=0; проверяем, что h^ имел mermaid, а h — нет
        before = count_mermaid_bytes(show(f"{h}^:{f}"))
        after = count_mermaid_bytes(show(f"{h}:{f}"))
        if before > 0 and after < before:
            return h, before, after
    return None

def collapse_to_other(merged_bytes):
    """Схлопывает конфликт-хунки merge-file ИЗБИРАТЕЛЬНО:
    - если other-сторона хунка содержит ```mermaid → берём other (восстанавливаем диаграмму);
    - иначе → берём cur (сохраняем поздние правки прозы, не откатываем лишнее).
    Формат: <<<<<<< cur \\n <cur> \\n ======= \\n <other> \\n >>>>>>> other
    Возвращает (resolved_bytes, n_took_other, n_kept_cur)."""
    lines = merged_bytes.split(b"\n")
    out = []
    i = 0
    took = 0
    kept = 0
    while i < len(lines):
        if lines[i].startswith(b"<<<<<<<"):
            i += 1
            cur_side = []
            while i < len(lines) and not lines[i].startswith(b"======="):
                cur_side.append(lines[i]); i += 1
            i += 1  # скип =======
            other_side = []
            while i < len(lines) and not lines[i].startswith(b">>>>>>>"):
                other_side.append(lines[i]); i += 1
            i += 1  # скип >>>>>>>
            if b"```mermaid" in b"\n".join(other_side):
                out.extend(other_side); took += 1
            else:
                out.extend(cur_side); kept += 1
        else:
            out.append(lines[i]); i += 1
    return b"\n".join(out), took, kept


def resolve_conflicts():
    import json as _json
    rep = _json.load(open(os.path.join(ROOT, "scripts", "restore_mermaid_report.json")))
    dirty = dirty_set()
    res = {"resolved": [], "still_dirty": [], "mismatch": []}
    for item in rep["conflict"]:
        f = item["f"]; h = item["C"]
        if f in dirty:
            res["still_dirty"].append(f); continue
        cur = pathlib.Path(ROOT, f).read_bytes()
        # найти полный hash коммита-удаления (в отчёте короткий)
        full = git_out("rev-parse", h)
        h = full.strip() if full else h
        base_b = show(f"{h}:{f}"); other_b = show(f"{h}^:{f}")
        with tempfile.TemporaryDirectory() as td:
            cp = os.path.join(td, "cur"); bp = os.path.join(td, "base"); op = os.path.join(td, "other")
            pathlib.Path(cp).write_bytes(cur); pathlib.Path(bp).write_bytes(base_b); pathlib.Path(op).write_bytes(other_b)
            r = subprocess.run(["git", "merge-file", "-p", cp, bp, op], cwd=ROOT, capture_output=True)
            merged = r.stdout
        resolved, took, kept = collapse_to_other(merged)
        want = count_mermaid_bytes(other_b); got = count_mermaid_bytes(resolved)
        # остались ли неразрешённые маркеры?
        if b"<<<<<<<" in resolved or b">>>>>>>" in resolved:
            res["mismatch"].append({"f": f, "want": want, "got": got, "note": "markers-left"}); continue
        if got != want:
            res["mismatch"].append({"f": f, "want": want, "got": got, "took": took, "kept": kept}); continue
        if APPLY:
            pathlib.Path(ROOT, f).write_bytes(resolved)
        res["resolved"].append({"f": f, "took_other": took, "kept_cur": kept, "blocks": want})
    print(json.dumps({k: (len(v) if isinstance(v, list) else v) for k, v in res.items()}, ensure_ascii=False, indent=2))
    for x in res["resolved"]:
        print(f"  resolved {x['f']}  took_other={x['took_other']}  kept_cur={x['kept_cur']}  blocks={x['blocks']}")
    for x in res["mismatch"]:
        print(f"  MISMATCH {x['f']}  want={x['want']} got={x['got']} hunks={x['hunks']}")
    return


def main():
    if "--resolve-conflicts" in sys.argv:
        resolve_conflicts(); return
    dirty = dirty_set()
    # текущие interview .md без mermaid
    files = []
    base = pathlib.Path(ROOT, "cheatsheets/interview")
    for p in base.rglob("*.md"):
        rel = str(p.relative_to(ROOT))
        if ONLY and not any(o in rel for o in ONLY):
            continue
        files.append(rel)
    files.sort()

    report = {"applied": [], "conflict": [], "no_history": [], "skip_dirty": [],
              "already_has": [], "mismatch": [], "path_gone": []}

    processed = 0
    for f in files:
        cur = pathlib.Path(ROOT, f).read_bytes()
        if count_mermaid_bytes(cur) > 0:
            report["already_has"].append(f)
            continue
        if f in dirty:
            report["skip_dirty"].append(f)
            continue
        rc = removal_commit(f)
        if not rc:
            report["no_history"].append(f)
            continue
        h, before, after = rc
        base_b = show(f"{h}:{f}")       # stripped
        other_b = show(f"{h}^:{f}")     # mermaid
        if base_b is None or other_b is None:
            report["path_gone"].append(f)
            continue
        with tempfile.TemporaryDirectory() as td:
            cp = os.path.join(td, "cur"); bp = os.path.join(td, "base"); op = os.path.join(td, "other")
            pathlib.Path(cp).write_bytes(cur)
            pathlib.Path(bp).write_bytes(base_b)
            pathlib.Path(op).write_bytes(other_b)
            # merge-file MUTATES cp in place; -p prints to stdout instead
            r = subprocess.run(["git", "merge-file", "-p", cp, bp, op], cwd=ROOT,
                               capture_output=True)
            merged = r.stdout
            clean = (r.returncode == 0)
        want = count_mermaid_bytes(other_b)
        got = count_mermaid_bytes(merged)
        if not clean:
            report["conflict"].append({"f": f, "C": h[:8], "want": want, "got_with_markers": got})
            continue
        if got != want:
            report["mismatch"].append({"f": f, "C": h[:8], "want": want, "got": got})
            continue
        if APPLY:
            pathlib.Path(ROOT, f).write_bytes(merged)
        report["applied"].append({"f": f, "C": h[:8], "blocks": want})
        processed += 1
        if LIMIT and processed >= LIMIT:
            break

    # summary
    print(json.dumps({k: (len(v) if isinstance(v, list) else v) for k, v in report.items()}, ensure_ascii=False, indent=2))
    print("\n--- applied (first 8) ---")
    for x in report["applied"][:8]:
        print(f"  {x['f']}  C={x['C']}  +{x['blocks']} mermaid")
    if report["conflict"]:
        print("\n--- conflicts (first 12) ---")
        for x in report["conflict"][:12]:
            print(f"  {x['f']}  C={x['C']}  want={x['want']}")
    if report["mismatch"]:
        print("\n--- mismatch (first 12) ---")
        for x in report["mismatch"][:12]:
            print(f"  {x['f']}  C={x['C']}  want={x['want']} got={x['got']}")
    # dump full report
    with open(os.path.join(ROOT, "scripts", "restore_mermaid_report.json"), "w") as fh:
        json.dump(report, fh, ensure_ascii=False, indent=2)
    print(f"\nfull report -> scripts/restore_mermaid_report.json   (APPLY={APPLY})")

if __name__ == "__main__":
    main()
