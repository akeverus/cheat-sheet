#!/usr/bin/env python3
r"""
Третий проход: почистить оставшиеся сложные нарушения.

Правки:
  1. Жирный внутри inline-кода: `...**X**...` → `...X...` (все `**` убираются)
  2. Жирный в любых скобках: (...**X**...) → (...X...) — включая случаи
     с запятыми, несколькими жирными, inline-кодом.
  3. Backticks в ссылках на URL: [`name`](url) → [name](url)
  4. Одиночное тире в расширенных контекстах: после точки/запятой/кавычки
     + ` - ` + буква → длинное тире.
  5. Комбинация `*X*` + другие близкие паттерны.

Проход делается только в прозе (не внутри code fence и frontmatter).
"""
from __future__ import annotations

import argparse
import re
from pathlib import Path

RE_FM = re.compile(r"^---\s*$")
RE_FENCE = re.compile(r"^(\s*)```")

# Удалить все ** внутри отдельного inline-кода `...`
# Pattern: backtick, затем любой символ не backtick, звёздочки и буквы,
# затем backtick. Мы применяем re.sub к `...` и внутри убираем **.
RE_INLINE_CODE = re.compile(r"`([^`\n]+)`")

# Жирный внутри скобок: любой паттерн (...**X**...) где есть ** пары
# Берём содержимое между ( и ) без вложенных скобок
RE_PARENS_CONTENT = re.compile(r"\(([^()]*)\)")

# Backticks в любых markdown-ссылках: [`name`](url) → [name](url)
RE_BACKTICK_ANY_LINK = re.compile(r"\[`([^`\]]+)`\]\(([^)]+)\)")

# Расширенное одиночное тире: после буквы/цифры/пунктуации + пробел + - + пробел + буква/цифра
# Добавляем запятую, точку, кавычку в допустимые предшественники
RE_DASH_WIDE = re.compile(
    r"([а-яА-ЯЁёA-Za-z0-9)»*,.\"»])\s-\s([а-яА-ЯЁёA-Za-z0-9(«*\"»])"
)

# Тире в начале bullet-пункта с русским словом: "- Текст - Другой текст"
# Уже покрыто выше.


def strip_bold_in_inline(text: str) -> tuple[str, int]:
    """Убрать ** внутри всех `...` inline-кодов."""
    n = [0]

    def repl(m: re.Match) -> str:
        content = m.group(1)
        new = content.replace("**", "")
        if new != content:
            n[0] += 1
        return f"`{new}`"

    result = RE_INLINE_CODE.sub(repl, text)
    return result, n[0]


def strip_bold_in_parens(text: str) -> tuple[str, int]:
    """Убрать ** из любых (...)."""
    n = [0]

    def repl(m: re.Match) -> str:
        content = m.group(1)
        if "**" not in content:
            return m.group(0)
        new = content.replace("**", "")
        n[0] += 1
        return f"({new})"

    # Применяем несколько раз для вложенных случаев (хотя regex не-рекурсивен)
    for _ in range(3):
        new_text = RE_PARENS_CONTENT.sub(repl, text)
        if new_text == text:
            break
        text = new_text
    return text, n[0]


def fix_backtick_link(match: re.Match) -> str:
    name = match.group(1)
    url = match.group(2)
    return f"[{name}]({url})"


def process_file(path: Path) -> tuple[bool, dict]:
    original = path.read_text(encoding="utf-8")
    lines = original.splitlines(keepends=False)
    out = []
    stats = {
        "bold_in_inline": 0,
        "bold_in_parens": 0,
        "backtick_link": 0,
        "dash_wide": 0,
    }

    in_fm = bool(lines and RE_FM.match(lines[0]))
    in_code = False
    for i, line in enumerate(lines):
        if in_fm and i > 0 and RE_FM.match(line):
            in_fm = False
            out.append(line); continue
        if in_fm:
            out.append(line); continue
        if RE_FENCE.match(line):
            in_code = not in_code
            out.append(line); continue
        if in_code:
            out.append(line); continue

        # 1) backticks в ссылках
        new_line = RE_BACKTICK_ANY_LINK.sub(fix_backtick_link, line)
        if new_line != line:
            stats["backtick_link"] += 1
            line = new_line

        # 2) ** в inline-коде
        line, n = strip_bold_in_inline(line)
        stats["bold_in_inline"] += n

        # 3) ** в скобках
        line, n = strip_bold_in_parens(line)
        stats["bold_in_parens"] += n

        # 4) расширенное тире
        before = line
        # Исключим строки-таблицы (начинаются с |) и строки кода
        if not line.lstrip().startswith("|"):
            line = RE_DASH_WIDE.sub(r"\1 — \2", line)
        if line != before:
            stats["dash_wide"] += 1

        out.append(line)

    new_text = "\n".join(out)
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
        if set(rel.parts) & exclude_dirs:
            continue
        files.append(p)
    return sorted(files)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path)
    parser.add_argument("--only", type=str)
    args = parser.parse_args()

    files = iter_md_files(args.root, {"interview"})
    if args.only:
        files = [f for f in files if args.only in str(f)]

    total = {k: 0 for k in ["bold_in_inline", "bold_in_parens", "backtick_link", "dash_wide"]}
    changed = 0
    for f in files:
        ch, stats = process_file(f)
        if ch:
            changed += 1
            for k, v in stats.items():
                total[k] += v

    print(f"Files scanned: {len(files)}")
    print(f"Files changed: {changed}")
    for k, v in total.items():
        print(f"  {k}: {v}")


if __name__ == "__main__":
    main()
