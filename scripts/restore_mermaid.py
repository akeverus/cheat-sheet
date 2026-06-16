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

def main():
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
