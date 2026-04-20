#!/usr/bin/env python3
"""Регенерировать TOC в каждом файле из реальных заголовков.

Стратегия:
1. Найти блок `## Содержание` в файле
2. Определить границы: от первой строки после заголовка до следующего `##` (не ## Содержание)
3. Собрать все H2/H3/H4 в файле (вне frontmatter, вне code fence)
4. Сгенерировать новый TOC с правильными якорями
5. Заменить старый блок

Формат:
- H2: `- [Title](#anchor)`
- H3: `  - [Title](#anchor)`
- H4: `    - [Title](#anchor)`

Исключения:
- `## Полезные ссылки` и `## Содержание` в TOC не включаем
- Если у файла нет `## Содержание` — пропускаем
"""
from __future__ import annotations

import re
from pathlib import Path

RE_FM = re.compile(r"^---\s*$")
RE_FENCE = re.compile(r"^(\s*)```")
RE_HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*$")
RE_TOC_HEADING = re.compile(r"^##\s+Содержание\s*$")
RE_ANY_H2 = re.compile(r"^##\s+")


def slugify(text: str) -> str:
    s = text.lower()
    s = re.sub(r"`([^`]+)`", r"\1", s)
    s = re.sub(r"\*\*([^*]+)\*\*", r"\1", s)
    s = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", s)
    s = re.sub(r"\[\[([^\]|]+)(?:\|[^\]]+)?\]\]", r"\1", s)
    # GFM допускает только буквы/цифры/дефисы/подчёркивания. Убираем всё остальное.
    # Пунктуация и спец-символы, кроме пробелов и дефисов.
    s = re.sub(r"[^\w\s-]", "", s, flags=re.UNICODE)
    # Пробелы → дефис
    s = re.sub(r"\s+", "-", s)
    s = re.sub(r"-+", "-", s)
    return s.strip("-")


EXCLUDE_FROM_TOC = {"Полезные ссылки", "Содержание"}


def collect_headings(lines: list[str]) -> list[tuple[int, int, str, str]]:
    """Возвращает список (line_no, level, text, slug) для всех заголовков вне frontmatter/code."""
    in_fm = bool(lines and RE_FM.match(lines[0]))
    in_code = False
    headings = []
    used = {}
    for i, line in enumerate(lines, 1):
        if in_fm and i > 1 and RE_FM.match(line):
            in_fm = False; continue
        if in_fm: continue
        if RE_FENCE.match(line):
            in_code = not in_code; continue
        if in_code: continue
        m = RE_HEADING.match(line)
        if m:
            level = len(m.group(1))
            text = m.group(2).strip()
            slug = slugify(text)
            if slug in used:
                used[slug] += 1
                slug = f"{slug}-{used[slug]}"
            else:
                used[slug] = 0
            headings.append((i, level, text, slug))
    return headings


def clean_heading_text(text: str) -> str:
    """Убрать backticks/жирный для текста TOC."""
    s = re.sub(r"`([^`]+)`", r"\1", text)
    s = re.sub(r"\*\*([^*]+)\*\*", r"\1", s)
    return s.strip()


def generate_toc(headings: list[tuple[int, int, str, str]], h1_line: int) -> list[str]:
    """Генерация TOC-блока."""
    out = []
    for lineno, level, text, slug in headings:
        if level == 1:
            continue  # H1 не включаем
        if level > 4:
            continue  # глубже H4 не идём
        if text in EXCLUDE_FROM_TOC:
            continue
        # Вложенность по уровню
        indent = "  " * (level - 2)
        clean_text = clean_heading_text(text)
        out.append(f"{indent}- [{clean_text}](#{slug})")
    return out


def process_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    if not lines:
        return False

    # Найти границы TOC
    toc_start = None
    toc_end = None
    for i, line in enumerate(lines):
        if RE_TOC_HEADING.match(line):
            toc_start = i
            break
    if toc_start is None:
        return False

    # Найти конец TOC: следующий ##, но НЕ сам Содержание
    for j in range(toc_start + 1, len(lines)):
        if RE_ANY_H2.match(lines[j]):
            toc_end = j
            break
    if toc_end is None:
        toc_end = len(lines)

    # Собрать заголовки
    headings = collect_headings(lines)
    h1_line = next((h[0] for h in headings if h[1] == 1), 0)
    # Фильтруем заголовки: только те что после H1, и только H2+ за пределами блока TOC
    filtered = [
        h for h in headings
        if h[1] >= 2 and h[0] > toc_end
    ]
    # Добавим сам "Полезные ссылки" если он до TOC (обычно он до)
    # На самом деле, не будем, оставим правило — TOC после Полезные ссылки.

    toc_entries = generate_toc(filtered, h1_line)
    if not toc_entries:
        return False

    # Новый TOC-блок
    new_toc = ["## Содержание", ""] + toc_entries + [""]

    new_lines = lines[:toc_start] + new_toc + lines[toc_end:]
    new_text = "\n".join(new_lines)
    if text.endswith("\n"):
        new_text += "\n"
    if new_text != text:
        path.write_text(new_text, encoding="utf-8")
        return True
    return False


def main():
    import sys
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("cheatsheets")
    changed = 0
    total = 0
    for p in sorted(root.rglob("*.md")):
        if "interview" in p.parts:
            continue
        if p.name == "CHEATSHEETS_ARCHITECTURE_AND_RULES.md":
            continue
        total += 1
        if process_file(p):
            changed += 1
    print(f"Files scanned: {total}")
    print(f"TOC regenerated: {changed}")


if __name__ == "__main__":
    main()
