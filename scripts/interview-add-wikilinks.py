#!/usr/bin/env python3
"""Добавить wikilinks в ## See also для interview-файлов с <5 wikilinks.

Адаптация scripts/cheatsheet-add-wikilinks.py для cheatsheets/interview/.

Логика:
1. Собрать frontmatter.tags для всех interview-файлов (кроме README.md, TOC.md)
2. Для каждого файла с <5 [[...]] — найти топ-N связанных по пересечению tags
3. Добавить в ## See also недостающие wikilinks (до 5 всего)
4. Если секции нет — создать в конце файла под --- разделитель

Важно:
- НЕ добавляем wikilinks на не-interview файлы (только внутри interview/)
- НЕ трогаем существующие wikilinks
- Wikilinks должны ссылаться на существующие .md файлы в interview/
- Формат: `- [[stem|Title]]` (title берётся из frontmatter)
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

RE_WIKILINK = re.compile(r"\[\[([^\]|]+)(?:\|[^\]]+)?\]\]")
TARGET_MIN = 5


def parse_fm(lines: list[str]) -> dict:
    if not lines or lines[0].strip() != "---":
        return {}
    fm_end = next((i for i, l in enumerate(lines[1:], 1) if l.strip() == "---"), None)
    if fm_end is None:
        return {}
    result: dict = {}
    in_tags = False
    tags: list[str] = []
    for line in lines[1:fm_end]:
        m_tags = re.match(r"^tags:\s*", line)
        if m_tags:
            in_tags = True
            continue
        if in_tags:
            m = re.match(r"^\s+-\s+(.+)", line)
            if m:
                tags.append(m.group(1).strip())
                continue
            elif not line.startswith(" "):
                in_tags = False
        m_title = re.match(r'^title:\s*["\']?(.+?)["\']?\s*$', line)
        if m_title:
            result["title"] = m_title.group(1).strip().strip('"\'')
    result["tags"] = tags
    return result


def get_existing_wikilinks(text: str) -> set[str]:
    return set(RE_WIKILINK.findall(text))


def find_see_also_bounds(lines: list[str]) -> tuple[int | None, int | None]:
    """Return (start_line_idx, end_line_idx_exclusive) of ## See also block."""
    start = None
    for i, line in enumerate(lines):
        if re.match(r"^##\s+See also\s*$", line):
            start = i
            break
    if start is None:
        return None, None
    # Find end: next ## or ### heading after the block
    for j in range(start + 1, len(lines)):
        if re.match(r"^#{2,3}\s+", lines[j]):
            return start, j
    return start, len(lines)


def process_file(path: Path, related_map: dict[str, list[tuple[str, str]]]) -> tuple[bool, int]:
    """Return (changed, added_count)."""
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    stem = path.stem

    existing = get_existing_wikilinks(text)
    total_brackets = len(re.findall(r"\[\[", text))
    if total_brackets >= TARGET_MIN:
        return False, 0

    # Get candidates: stems not already wikilinked and not self
    candidates = [
        (s, title) for s, title in related_map.get(stem, [])
        if s not in existing and s != stem
    ]
    needed = TARGET_MIN - total_brackets
    to_add = candidates[:needed]
    if not to_add:
        return False, 0

    new_entries = [f"- [[{s}|{title}]]" for s, title in to_add]

    see_also_start, see_also_end = find_see_also_bounds(lines)

    if see_also_start is not None:
        # Insert at end of existing block, trimming trailing blank lines
        insert_pos = see_also_end if see_also_end is not None else len(lines)
        # Skip trailing blank lines to append right after last bullet
        while insert_pos > see_also_start + 1 and lines[insert_pos - 1].strip() == "":
            insert_pos -= 1
        new_lines = lines[:insert_pos] + new_entries + lines[insert_pos:]
    else:
        # No ## See also — append new section at the end
        # Drop trailing blank lines
        while lines and lines[-1].strip() == "":
            lines.pop()
        block = ["", "---", "", "## See also"] + new_entries
        new_lines = lines + block

    new_text = "\n".join(new_lines)
    if text.endswith("\n"):
        new_text += "\n"
    if new_text != text:
        path.write_text(new_text, encoding="utf-8")
        return True, len(new_entries)
    return False, 0


def build_related_map(root: Path) -> dict[str, list[tuple[str, str]]]:
    """stem → [(related_stem, title), ...] sorted by tag overlap score desc.

    Only interview-files are considered (root = cheatsheets/interview).
    README.md и TOC.md исключаются.
    """
    files_meta: dict[str, dict] = {}
    for p in root.rglob("*.md"):
        if p.name in ("README.md", "TOC.md"):
            continue
        lines = p.read_text(encoding="utf-8").splitlines()
        fm = parse_fm(lines)
        files_meta[p.stem] = {
            "tags": fm.get("tags", []),
            "title": fm.get("title", p.stem),
        }

    # tag → stems
    tag_index: dict[str, list[str]] = {}
    for stem, d in files_meta.items():
        for tag in d["tags"]:
            tag_index.setdefault(tag, []).append(stem)

    related_map: dict[str, list[tuple[str, str]]] = {}
    for stem, d in files_meta.items():
        scores: dict[str, int] = {}
        for tag in d["tags"]:
            for other in tag_index.get(tag, []):
                if other != stem:
                    scores[other] = scores.get(other, 0) + 1
        sorted_others = sorted(scores.items(), key=lambda x: (-x[1], x[0]))
        related_map[stem] = [(s, files_meta[s]["title"]) for s, _ in sorted_others]
    return related_map


def main():
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("cheatsheets/interview")
    only = sys.argv[2] if len(sys.argv) > 2 else None

    if not root.exists():
        print(f"Root does not exist: {root}")
        sys.exit(1)

    print(f"Root: {root}")
    print("Building related map (interview-only)...")
    related_map = build_related_map(root)

    changed = total = added_total = 0
    for p in sorted(root.rglob("*.md")):
        if p.name in ("README.md", "TOC.md"):
            continue
        if only and only not in str(p):
            continue
        text = p.read_text(encoding="utf-8")
        if len(re.findall(r"\[\[", text)) >= TARGET_MIN:
            continue
        total += 1
        ok, added = process_file(p, related_map)
        if ok:
            changed += 1
            added_total += added
            print(f"  Updated (+{added}): {p.relative_to(root)}")

    print(f"\nFiles scanned (with <{TARGET_MIN} wikilinks): {total}")
    print(f"Updated: {changed}")
    print(f"Total wikilinks added: {added_total}")


if __name__ == "__main__":
    main()
