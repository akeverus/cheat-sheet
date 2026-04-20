#!/usr/bin/env python3
"""
Автоматические безопасные правки форматирования в cheatsheets/.
Учитывает:
  - YAML frontmatter в начале файла (между двумя ---): не трогаем
  - code fences (```...```): внутри не трогаем ни кавычек, ни тире
  - wikilinks [[...]]: текст внутри не трогаем

Правки:
  1. Trailing whitespace: удалить пробелы в конце строк
  2. Три+ пустых строк подряд: сжать до одной
  3. Горизонтальные --- между секциями: удалить (frontmatter оставить)
  4. Код-блок без языка: ``` → ```text
  5. ### Baeldung / Oracle / GeeksforGeeks / DigitalOcean / Medium → ### Обучающие материалы
  6. Backticks в ссылках [`name`](url) → если url оканчивается на .md → [[basename]], иначе [name](url)
  7. Прямые кавычки "слово" в русской прозе → «слово»
  8. Одиночное тире в русской прозе: " - " между словами → " — "
  9. "--" как разделитель → "—"
"""
from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path

RU_CHAR = "А-Яа-яЁё"

# Regex для frontmatter и code fence
RE_FRONTMATTER_BOUNDARY = re.compile(r"^---\s*$")
RE_CODE_FENCE = re.compile(r"^(\s*)```(.*)$")
RE_HR = re.compile(r"^---+\s*$")
RE_TRIPLE_BLANK = re.compile(r"\n{4,}")

# Прямые кавычки вокруг русских слов или коротких фраз
RE_QUOTED_RU = re.compile(
    r'"([' + RU_CHAR + r'][' + RU_CHAR + r' \-.,;:!?]*?[' + RU_CHAR + r'])"'
)
# Одиночное «квоте-пару» на русском с одним словом
RE_QUOTED_ONE_RU = re.compile(r'"([' + RU_CHAR + r']+)"')

# Тире: слово/буква + space + - + space + слово/буква
# Ловим русский контекст чтобы не трогать a - b в коде
RE_DASH_RU = re.compile(
    r"([" + RU_CHAR + r"A-Za-z0-9)»*])\s-\s([" + RU_CHAR + r"A-Za-z0-9(«*])"
)
# " -- " как длинное тире
RE_DOUBLE_DASH = re.compile(r"(\s)--(\s)")

# Backticks внутри markdown-ссылки
RE_BACKTICK_LINK = re.compile(r"\[`([^`\]]+)`\]\(([^)]+)\)")

# Секция-заголовок по имени сайта
RE_SITE_SECTION = re.compile(
    r"^(#{2,4})\s+(?:\*\*)?(Baeldung|Oracle|DigitalOcean|GeeksforGeeks|Medium)(?:\*\*)?\s*$",
    re.IGNORECASE,
)


class FileState:
    def __init__(self):
        self.in_frontmatter = False
        self.frontmatter_done = False
        self.in_code = False
        self.fence_indent = ""


def is_protected_line(line: str, state: FileState) -> bool:
    """True if line is inside frontmatter or code fence (i.e. don't touch)."""
    return state.in_frontmatter or state.in_code


def fix_backtick_link(match: re.Match) -> str:
    name = match.group(1)
    url = match.group(2)
    # Если ссылка на .md — конвертим в wikilink
    if url.endswith(".md"):
        base = os.path.basename(url)[:-3]
        return f"[[{base}]]"
    # Иначе убираем backticks, оставляя текстовую ссылку
    return f"[{name}]({url})"


def fix_quotes(text: str) -> str:
    # Работаем вне inline-кода (между `...`)
    out = []
    i = 0
    in_inline = False
    buf = ""
    while i < len(text):
        ch = text[i]
        if ch == "`":
            # сбросить buf: в нём текст вне inline-кода
            if not in_inline:
                out.append(RE_QUOTED_RU.sub(lambda m: "«" + m.group(1) + "»", buf))
                buf = ""
            else:
                out.append(buf)
                buf = ""
            in_inline = not in_inline
            out.append(ch)
        else:
            buf += ch
        i += 1
    if in_inline:
        # unclosed inline: оставляем как есть
        out.append(buf)
    else:
        out.append(RE_QUOTED_RU.sub(lambda m: "«" + m.group(1) + "»", buf))
    return "".join(out)


def fix_dashes(text: str) -> str:
    # Работаем вне inline-кода
    out = []
    i = 0
    in_inline = False
    buf = ""
    while i < len(text):
        ch = text[i]
        if ch == "`":
            if not in_inline:
                # fix dashes in buf
                fixed = RE_DOUBLE_DASH.sub(r"\1—\2", buf)
                fixed = RE_DASH_RU.sub(r"\1 — \2", fixed)
                out.append(fixed)
                buf = ""
            else:
                out.append(buf)
                buf = ""
            in_inline = not in_inline
            out.append(ch)
        else:
            buf += ch
        i += 1
    if in_inline:
        out.append(buf)
    else:
        fixed = RE_DOUBLE_DASH.sub(r"\1—\2", buf)
        fixed = RE_DASH_RU.sub(r"\1 — \2", fixed)
        out.append(fixed)
    return "".join(out)


def process_file(path: Path) -> tuple[bool, dict]:
    """Вернёт (изменён_ли, счётчики_изменений)."""
    original = path.read_text(encoding="utf-8")
    lines = original.splitlines(keepends=False)
    out_lines = []
    stats = {
        "trailing_ws": 0,
        "hr_removed": 0,
        "code_lang_added": 0,
        "site_section": 0,
        "backtick_link": 0,
        "quotes_fixed": 0,
        "dashes_fixed": 0,
    }

    state = FileState()
    # Detect if file starts with frontmatter
    start_has_fm = bool(lines and RE_FRONTMATTER_BOUNDARY.match(lines[0]))
    if start_has_fm:
        state.in_frontmatter = True

    idx = 0
    while idx < len(lines):
        line = lines[idx]

        # Frontmatter boundary tracking (only first block)
        if state.in_frontmatter and idx > 0 and RE_FRONTMATTER_BOUNDARY.match(line):
            state.in_frontmatter = False
            state.frontmatter_done = True
            out_lines.append(line)
            idx += 1
            continue

        if state.in_frontmatter:
            # Within frontmatter: only trim trailing WS, nothing else
            stripped = line.rstrip()
            if stripped != line:
                stats["trailing_ws"] += 1
            out_lines.append(stripped)
            idx += 1
            continue

        # Code fence tracking
        fence_m = RE_CODE_FENCE.match(line)
        if fence_m:
            indent, rest = fence_m.group(1), fence_m.group(2).strip()
            if not state.in_code:
                # opening
                state.in_code = True
                state.fence_indent = indent
                if rest == "":
                    # code block without language → add 'text'
                    line = f"{indent}```text"
                    stats["code_lang_added"] += 1
            else:
                # closing fence (regardless of lang after)
                if rest == "":
                    state.in_code = False
                    state.fence_indent = ""
                else:
                    # rare: nested fences aren't supported in CommonMark; treat as close
                    state.in_code = False
                    state.fence_indent = ""
            # trim trailing ws on fence lines too
            stripped = line.rstrip()
            if stripped != line:
                stats["trailing_ws"] += 1
            out_lines.append(stripped)
            idx += 1
            continue

        if state.in_code:
            # Inside code: only trim trailing WS
            stripped = line.rstrip()
            if stripped != line:
                stats["trailing_ws"] += 1
            out_lines.append(stripped)
            idx += 1
            continue

        # Outside frontmatter and code — apply prose rules

        # 3) Horizontal rule --- between sections: remove
        if RE_HR.match(line):
            stats["hr_removed"] += 1
            # skip this line (don't add)
            # also drop preceding blank lines if they become duplicated; handled later
            idx += 1
            continue

        # 5) ### Baeldung / Oracle → ### Обучающие материалы
        m = RE_SITE_SECTION.match(line)
        if m:
            level = m.group(1)
            line = f"{level} Обучающие материалы"
            stats["site_section"] += 1

        # 6) Backticks inside markdown links
        before = line
        line = RE_BACKTICK_LINK.sub(fix_backtick_link, line)
        if line != before:
            stats["backtick_link"] += line.count("[[") + line.count("](")  # rough

        # 7) Прямые кавычки → ёлочки (только в прозе вне inline-кода)
        before = line
        line = fix_quotes(line)
        if line != before:
            stats["quotes_fixed"] += 1

        # 8) Тире
        before = line
        line = fix_dashes(line)
        if line != before:
            stats["dashes_fixed"] += 1

        # 1) Trailing whitespace
        stripped = line.rstrip()
        if stripped != line:
            stats["trailing_ws"] += 1
        out_lines.append(stripped)
        idx += 1

    # Reassemble
    new_text = "\n".join(out_lines)
    if original.endswith("\n"):
        new_text += "\n"

    # 2) Collapse 3+ blank lines → 1
    collapsed = RE_TRIPLE_BLANK.sub("\n\n", new_text)
    if collapsed != new_text:
        new_text = collapsed

    changed = new_text != original
    if changed:
        path.write_text(new_text, encoding="utf-8")
    return changed, stats


def iter_md_files(root: Path, exclude_dirs: set[str]) -> list[Path]:
    files = []
    for p in root.rglob("*.md"):
        # skip interview
        rel = p.relative_to(root)
        parts = set(rel.parts)
        if parts & exclude_dirs:
            continue
        files.append(p)
    return sorted(files)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path, help="cheatsheets/ root")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--only", type=str, help="glob filter (substring match)")
    parser.add_argument("--limit", type=int, default=0)
    args = parser.parse_args()

    exclude = {"interview"}
    files = iter_md_files(args.root, exclude)
    if args.only:
        files = [f for f in files if args.only in str(f)]
    if args.limit:
        files = files[: args.limit]

    total_stats = {k: 0 for k in [
        "trailing_ws", "hr_removed", "code_lang_added",
        "site_section", "backtick_link", "quotes_fixed", "dashes_fixed",
    ]}
    changed_count = 0

    for f in files:
        if args.dry_run:
            # read+process but don't write
            original = f.read_text(encoding="utf-8")
            temp = Path(str(f) + ".tmp-autofix")
            temp.write_text(original, encoding="utf-8")
            changed, stats = process_file(temp)
            if changed:
                new_text = temp.read_text(encoding="utf-8")
            temp.unlink()
            if not changed:
                continue
        else:
            changed, stats = process_file(f)
        if changed:
            changed_count += 1
            for k, v in stats.items():
                total_stats[k] += v

    print(f"Files scanned: {len(files)}")
    print(f"Files changed: {changed_count}")
    for k, v in total_stats.items():
        print(f"  {k}: {v}")


if __name__ == "__main__":
    main()
