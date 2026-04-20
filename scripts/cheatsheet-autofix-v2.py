#!/usr/bin/env python3
"""
Второй проход: снять жирный в заголовках и скобках, конвертировать
relative-markdown-ссылки в wikilinks.

Правки:
  1. Жирный в заголовках: ^(#+)\s+...**X**... → ^(#+)\s+...X...
  2. Жирный в скобках (**X**) → (X) [если внутри только один жирный]
  3. Комбинация `**X**` → `X` (убираем жирный внутри backticks)
  4. Markdown-ссылка [text](path.md) → [[basename]] если файл существует
     в cheatsheets/ (рекурсивно).
"""
from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path

RE_FRONTMATTER_BOUNDARY = re.compile(r"^---\s*$")
RE_CODE_FENCE = re.compile(r"^(\s*)```(.*)$")
RE_HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*$")
# Жирный в скобках с одним **...** внутри
RE_BOLD_PARENS = re.compile(r"\(\*\*([^*()]+)\*\*\)")
# Комбинация: backtick-bold-backtick на уровне строки
RE_BACKTICK_BOLD = re.compile(r"`\*\*([^*`]+)\*\*`")
RE_BOLD_BACKTICK = re.compile(r"\*\*`([^`*]+)`\*\*")
# Markdown-ссылка вида [text](path.md) или [text](../something.md#anchor)
RE_MD_LINK = re.compile(r"\[([^\]]+)\]\(([^)\s]+\.md)(#[^)]*)?\)")

_md_index_cache: dict[str, set[Path]] = {}


def build_md_index(root: Path) -> dict[str, Path]:
    """basename (без .md) → Path относительно root."""
    idx: dict[str, Path] = {}
    for p in root.rglob("*.md"):
        base = p.stem
        # Для конфликтующих имён: оставляем первую попавшуюся (но строим set)
        if base not in idx:
            idx[base] = p.relative_to(root)
    return idx


def strip_bold_in_heading(text: str) -> str:
    """Убрать все **...** в строке, сохранив содержимое."""
    return re.sub(r"\*\*([^*]+)\*\*", r"\1", text)


def fix_md_link(match: re.Match, md_index: dict[str, Path], current_file: Path, root: Path) -> str:
    text = match.group(1)
    path = match.group(2)
    anchor = match.group(3) or ""

    # Пропускаем внешние URL и абсолютные пути
    if path.startswith(("http://", "https://", "/")):
        return match.group(0)

    # Пропускаем ссылки на non-cheatsheet (../../README.md и т.п.)
    if path.startswith("../") and not path.startswith("../") and "cheatsheets" not in path:
        pass  # разберёмся ниже

    # Разрешаем путь относительно текущего файла
    current_dir = (current_file.parent).resolve()
    try:
        target = (current_dir / path).resolve()
    except Exception:
        return match.group(0)

    # Проверяем, что цель внутри cheatsheets/
    try:
        rel_to_root = target.relative_to(root.resolve())
    except ValueError:
        # Цель вне cheatsheets → оставляем markdown-ссылку
        return match.group(0)

    # Файл должен существовать
    if not target.exists() or not target.is_file():
        return match.group(0)

    base = target.stem

    # Если текст ссылки совпадает с именем файла или неинформативен — чистый wikilink
    simple_text = text.strip("` *_")
    if simple_text.lower() == base.lower() or simple_text == path:
        if anchor:
            return f"[[{base}{anchor}]]"
        return f"[[{base}]]"
    # Иначе wikilink с альтернативным текстом
    if anchor:
        return f"[[{base}{anchor}|{text}]]"
    return f"[[{base}|{text}]]"


def process_file(path: Path, root: Path, md_index: dict[str, Path]) -> tuple[bool, dict]:
    original = path.read_text(encoding="utf-8")
    lines = original.splitlines(keepends=False)
    out_lines = []
    stats = {
        "heading_bold": 0,
        "bold_parens": 0,
        "backtick_bold_combo": 0,
        "md_to_wikilink": 0,
    }

    in_frontmatter = bool(lines and RE_FRONTMATTER_BOUNDARY.match(lines[0]))
    in_code = False

    for i, line in enumerate(lines):
        # Frontmatter
        if in_frontmatter and i > 0 and RE_FRONTMATTER_BOUNDARY.match(line):
            in_frontmatter = False
            out_lines.append(line)
            continue
        if in_frontmatter:
            out_lines.append(line)
            continue

        # Code fence tracking
        fence_m = RE_CODE_FENCE.match(line)
        if fence_m:
            in_code = not in_code
            out_lines.append(line)
            continue
        if in_code:
            out_lines.append(line)
            continue

        # Heading: снять жирный
        h = RE_HEADING.match(line)
        if h:
            level, title = h.group(1), h.group(2)
            if "**" in title:
                new_title = strip_bold_in_heading(title)
                if new_title != title:
                    stats["heading_bold"] += 1
                    line = f"{level} {new_title}"
            out_lines.append(line)
            continue

        # Жирный в скобках: (**X**) → (X)
        new_line = RE_BOLD_PARENS.sub(r"(\1)", line)
        if new_line != line:
            stats["bold_parens"] += line.count("(**")
            line = new_line

        # Комбинация `**X**` → `X` и **`X`** → `X`
        new_line = RE_BACKTICK_BOLD.sub(r"`\1`", line)
        if new_line != line:
            stats["backtick_bold_combo"] += 1
            line = new_line
        new_line = RE_BOLD_BACKTICK.sub(r"`\1`", line)
        if new_line != line:
            stats["backtick_bold_combo"] += 1
            line = new_line

        # md → wikilink
        new_line = RE_MD_LINK.sub(
            lambda m: fix_md_link(m, md_index, path, root), line
        )
        if new_line != line:
            stats["md_to_wikilink"] += new_line.count("[[")
            line = new_line

        out_lines.append(line)

    new_text = "\n".join(out_lines)
    if original.endswith("\n"):
        new_text += "\n"

    changed = new_text != original
    if changed:
        path.write_text(new_text, encoding="utf-8")
    return changed, stats


def iter_md_files(root: Path, exclude_dirs: set[str]) -> list[Path]:
    files = []
    for p in root.rglob("*.md"):
        rel = p.relative_to(root)
        parts = set(rel.parts)
        if parts & exclude_dirs:
            continue
        files.append(p)
    return sorted(files)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path)
    parser.add_argument("--only", type=str)
    parser.add_argument("--limit", type=int, default=0)
    args = parser.parse_args()

    exclude = {"interview"}
    files = iter_md_files(args.root, exclude)
    if args.only:
        files = [f for f in files if args.only in str(f)]
    if args.limit:
        files = files[: args.limit]

    root = args.root.resolve()
    md_index = build_md_index(root)

    total = {k: 0 for k in ["heading_bold", "bold_parens", "backtick_bold_combo", "md_to_wikilink"]}
    changed_count = 0
    for f in files:
        changed, stats = process_file(f, root, md_index)
        if changed:
            changed_count += 1
            for k, v in stats.items():
                total[k] += v

    print(f"Files scanned: {len(files)}")
    print(f"Files changed: {changed_count}")
    for k, v in total.items():
        print(f"  {k}: {v}")


if __name__ == "__main__":
    main()
