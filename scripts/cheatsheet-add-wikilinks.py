#!/usr/bin/env python3
"""Добавить wikilinks в ### См. также для файлов с <5 wikilinks.

Логика:
1. Собрать frontmatter.tags для всех файлов
2. Для каждого файла с <5 [[...]] — найти топ-N связанных по тегам
3. Добавить в ### См. также недостающие wikilinks (до 5 всего)
4. Если секции нет — создать под ## Полезные ссылки
"""
from __future__ import annotations
import re
import sys
from pathlib import Path

RE_FM = re.compile(r"^---\s*$")
RE_FENCE = re.compile(r"^```")
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
    """Return (start_line_idx, end_line_idx_exclusive) of ### См. также block."""
    start = None
    for i, line in enumerate(lines):
        if re.match(r"^###\s+См\. также\s*$", line):
            start = i
            break
    if start is None:
        return None, None
    # Find end: next ## or ### heading (other than itself)
    for j in range(start + 1, len(lines)):
        if re.match(r"^#{2,3}\s+", lines[j]):
            return start, j
    return start, len(lines)


def find_useful_links_pos(lines: list[str]) -> int | None:
    """Return line index of ## Полезные ссылки."""
    for i, line in enumerate(lines):
        if re.match(r"^##\s+Полезные ссылки\s*$", line):
            return i
    return None


def process_file(path: Path, related_map: dict[str, list[tuple[str, str]]]) -> bool:
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    stem = path.stem

    existing = get_existing_wikilinks(text)
    if len(existing) >= TARGET_MIN:
        return False

    # Get candidates: stems not already wikilinked and not self
    candidates = [
        (s, title) for s, title in related_map.get(stem, [])
        if s not in existing and s != stem
    ]
    needed = TARGET_MIN - len(existing)
    to_add = candidates[:needed]
    if not to_add:
        return False

    new_entries = [f"- [[{s}|{title}]]" for s, title in to_add]

    see_also_start, see_also_end = find_see_also_bounds(lines)

    if see_also_start is not None:
        # Insert after existing entries, before the end
        insert_pos = see_also_end if see_also_end is not None else len(lines)
        # Insert before the next heading
        new_lines = lines[:insert_pos] + new_entries + lines[insert_pos:]
    else:
        # No ### См. также — create it under ## Полезные ссылки
        ul_pos = find_useful_links_pos(lines)
        if ul_pos is None:
            # No ## Полезные ссылки either — skip
            return False
        # Find end of ## Полезные ссылки block (next ## heading, or ## Содержание)
        insert_pos = ul_pos + 1
        for j in range(ul_pos + 1, len(lines)):
            if re.match(r"^##\s+", lines[j]):
                insert_pos = j
                break
        else:
            insert_pos = len(lines)
        # Insert ### См. также block
        block = ["", "### См. также"] + new_entries
        new_lines = lines[:insert_pos] + block + lines[insert_pos:]

    new_text = "\n".join(new_lines)
    if text.endswith("\n"):
        new_text += "\n"
    if new_text != text:
        path.write_text(new_text, encoding="utf-8")
        return True
    return False


def build_related_map(root: Path) -> dict[str, list[tuple[str, str]]]:
    """stem → [(related_stem, title), ...] sorted by tag overlap score desc."""
    files_meta: dict[str, dict] = {}
    for p in root.rglob("*.md"):
        if "interview" in p.parts or p.name == "CHEATSHEETS_ARCHITECTURE_AND_RULES.md":
            continue
        lines = p.read_text(encoding="utf-8").splitlines()
        fm = parse_fm(lines)
        files_meta[p.stem] = {"tags": fm.get("tags", []), "title": fm.get("title", p.stem)}

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
        sorted_others = sorted(scores.items(), key=lambda x: -x[1])
        related_map[stem] = [(s, files_meta[s]["title"]) for s, _ in sorted_others]
    return related_map


def main():
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("cheatsheets")
    only = sys.argv[2] if len(sys.argv) > 2 else None

    print("Building related map...")
    related_map = build_related_map(root)

    changed = total = 0
    for p in sorted(root.rglob("*.md")):
        if "interview" in p.parts or p.name == "CHEATSHEETS_ARCHITECTURE_AND_RULES.md":
            continue
        if only and only not in str(p):
            continue
        text = p.read_text(encoding="utf-8")
        if len(re.findall(r"\[\[", text)) >= TARGET_MIN:
            continue
        total += 1
        if process_file(p, related_map):
            changed += 1
            print(f"  Updated: {p.relative_to(root)}")

    print(f"\nFiles scanned (with <{TARGET_MIN} wikilinks): {total}")
    print(f"Updated: {changed}")


if __name__ == "__main__":
    main()
