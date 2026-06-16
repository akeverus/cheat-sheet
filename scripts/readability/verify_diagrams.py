#!/usr/bin/env python3
"""verify_diagrams.py <md-path>

Гейт для волны восстановления диаграмм. Сверяет рабочую версию файла с git HEAD.
Инвариант: мы ТОЛЬКО ДОБАВЛЯЕМ не-mermaid диаграммы (ASCII/box-drawing в ``` или
таблицы) — всё остальное структурно неизменно.

PASS только если ВСЕ выполнено:
  1. 0 блоков ```mermaid.
  2. Множество и порядок заголовков (## и ###) байт-идентичны HEAD
     (не трогаем вопросы → TOC остаётся согласованным).
  3. Frontmatter (между первыми двумя ---) байт-идентичен HEAD.
  4. Каждый fenced-code-блок из HEAD присутствует в новой версии дословно
     (существующий код/таблицы-в-фенсах не редактировались и не потеряны).
  5. Баланс ``` (чётное число fence-маркеров).
  6. Добавлен хотя бы один новый fenced-блок (мы реально вернули диаграммы)
     — если 0, это WARN, не FAIL (файл мог быть без диаграмм).
  7. 0 MCQ-callouts (> [!mcq]) и 0 Tier-1 маркеров (❌ ПОСЛЕДСТВИЕ/✓ ПРИМЕНЯТЬ/
     📋 ПРАВИЛО/🔗 См. Q).

Печатает 'PASS <file>' или 'FAIL <file>' + список нарушений; код выхода 0/1.
"""
import re
import subprocess
import sys


def git_head(path):
    r = subprocess.run(["git", "show", f"HEAD:{path}"],
                       capture_output=True, text=True)
    if r.returncode != 0:
        return None
    return r.stdout


def fenced_blocks(text):
    """Список тел fenced-блоков (между ``` и ```), с языковым тегом."""
    lines = text.splitlines()
    blocks = []
    i = 0
    while i < len(lines):
        s = lines[i].strip()
        if s.startswith("```"):
            lang = s[3:].strip().lower()
            body = []
            j = i + 1
            while j < len(lines) and not lines[j].strip().startswith("```"):
                body.append(lines[j])
                j += 1
            blocks.append((lang, "\n".join(body)))
            i = j + 1
            continue
        i += 1
    return blocks


def headings(text):
    return [ln for ln in text.splitlines()
            if ln.startswith("## ") or ln.startswith("### ")]


def frontmatter(text):
    lines = text.splitlines()
    if not lines or lines[0].strip() != "---":
        return ""
    out = []
    for ln in lines[1:]:
        if ln.strip() == "---":
            break
        out.append(ln)
    return "\n".join(out)


def main():
    path = sys.argv[1]
    with open(path, encoding="utf-8") as fh:
        new = fh.read()
    head = git_head(path)

    problems = []
    warns = []

    # 1. mermaid
    n_merm = sum(1 for ln in new.splitlines()
                 if ln.strip().lower().startswith("```mermaid"))
    if n_merm:
        problems.append(f"{n_merm} ```mermaid block(s) present")

    # 5. fence balance
    n_fence = sum(1 for ln in new.splitlines() if ln.strip().startswith("```"))
    if n_fence % 2 != 0:
        problems.append(f"odd ``` fence count ({n_fence}) — unbalanced")

    # 7. mcq / tier-1
    if "> [!mcq]" in new:
        problems.append("MCQ-callout present")
    for marker in ("❌ ПОСЛЕДСТВИЕ", "✓ ПРИМЕНЯТЬ", "📋 ПРАВИЛО", "🔗 См. Q"):
        if marker in new:
            problems.append(f"Tier-1 marker present: {marker}")

    if head is not None:
        # 2. headings identical
        if headings(new) != headings(head):
            problems.append("heading set/order changed vs HEAD "
                            "(questions or sub-headings altered)")
        # 3. frontmatter identical
        if frontmatter(new) != frontmatter(head):
            problems.append("frontmatter changed vs HEAD")
        # 4. every HEAD fenced block preserved verbatim
        new_bodies = [b for _, b in fenced_blocks(new)]
        new_bodies_set = list(new_bodies)
        head_blocks = fenced_blocks(head)
        # multiset preservation
        tmp = list(new_bodies_set)
        missing = 0
        for lang, body in head_blocks:
            if body in tmp:
                tmp.remove(body)
            else:
                missing += 1
        if missing:
            problems.append(f"{missing} HEAD fenced code-block(s) not found "
                            "verbatim in new file (existing code edited/lost)")
        # 6. new fenced blocks added?
        added = len(fenced_blocks(new)) - len(head_blocks)
        if added <= 0:
            warns.append(f"no new fenced block added (delta={added})")
    else:
        warns.append("no HEAD version (new/untracked file) — skipped diff checks")

    name = path
    if problems:
        print(f"FAIL {name}")
        for p in problems:
            print(f"  - {p}")
        for w in warns:
            print(f"  ~ {w}")
        sys.exit(1)
    print(f"PASS {name}" + (f"  (+{len(fenced_blocks(new)) - (len(fenced_blocks(head)) if head else 0)} fenced)" if head else ""))
    for w in warns:
        print(f"  ~ {w}")
    sys.exit(0)


if __name__ == "__main__":
    main()
