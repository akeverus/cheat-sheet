#!/usr/bin/env python3
"""
Lint-скрипт для cheatsheets/.
Проверяет правила из CHEATSHEETS_ARCHITECTURE_AND_RULES.md (раздел «Инлайн-форматирование»).

Использование:
  scripts/cheatsheet-lint.py cheatsheets            # полный отчёт
  scripts/cheatsheet-lint.py cheatsheets --summary  # только итоговая сводка
  scripts/cheatsheet-lint.py cheatsheets --rule heading-bold  # конкретное правило

Exit code:
  0 — нарушений не найдено
  1 — найдены нарушения
"""
from __future__ import annotations

import argparse
import re
import sys
from collections import defaultdict
from pathlib import Path

RE_FM = re.compile(r"^---\s*$")
RE_FENCE = re.compile(r"^(\s*)```(.*)$")
RE_HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*$")


# Все правила: (id, описание, паттерн-проверки)
class Rule:
    def __init__(self, rid: str, desc: str):
        self.id = rid
        self.desc = desc
        self.violations: list[tuple[Path, int, str]] = []

    def add(self, path: Path, lineno: int, line: str):
        self.violations.append((path, lineno, line.rstrip()))


def iter_lines_with_context(path: Path):
    """Yields (lineno, line, in_frontmatter, in_code). 1-based lineno."""
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    in_fm = bool(lines and RE_FM.match(lines[0]))
    in_code = False
    for i, line in enumerate(lines, start=1):
        if in_fm and i > 1 and RE_FM.match(line):
            in_fm = False
            yield i, line, True, False
            continue
        if in_fm:
            yield i, line, True, False
            continue
        fm = RE_FENCE.match(line)
        if fm:
            was_in = in_code
            in_code = not in_code
            yield i, line, False, was_in or not in_code  # fence line itself is "code"
            continue
        yield i, line, False, in_code


def check_file(path: Path, root: Path, md_index: dict[str, Path], rules: dict[str, Rule]):
    """Применяет все правила к файлу."""
    # Для битых wikilinks
    RE_WIKILINK = re.compile(r"\[\[([^\]|#]+)(?:\|[^\]]+)?(?:#[^\]]+)?\]\]")
    # Ссылки на относительные .md
    RE_MD_LINK = re.compile(r"\[([^\]]+)\]\(([^)\s]+\.md)(?:#[^)]*)?\)")
    # Backticks в скобках ссылок
    RE_BACKTICK_LINK = re.compile(r"\[`[^`\]]+`\]")
    # Site-секции
    RE_SITE_SEC = re.compile(r"^#{2,4}\s+(?:\*\*)?(Baeldung|Oracle|DigitalOcean|GeeksforGeeks|Medium)\*?\*?\s*$", re.I)
    # Прямые кавычки вокруг русского
    RE_QUOTED_RU = re.compile(r'"[А-ЯЁа-яё][^"]{0,80}[А-ЯЁа-яё]"')
    # Одиночное тире в прозе
    RE_DASH_PROSE = re.compile(r"[а-яёА-ЯЁa-zA-Z0-9)»]\s-\s[а-яёА-ЯЁa-zA-Z0-9(«]")
    # Backtick+bold
    RE_BT_BOLD = re.compile(r"`\*\*[^*`]+\*\*`|\*\*`[^`*]+`\*\*")
    # Жирный внутри скобок (простой случай)
    RE_BOLD_PARENS = re.compile(r"\(\*\*[^*()]+\*\*\)")
    # Для очистки строки от inline-кода и markdown-ссылок (чтобы не ловить там ложные нарушения)
    RE_INLINE_CODE_STRIP = re.compile(r"`[^`\n]+`")
    RE_LINK_STRIP = re.compile(r"\[[^\]]*\]\([^)]+\)")

    def prose_only(text: str) -> str:
        """Удалить inline-код и markdown-ссылки — оставить только прозу."""
        text = RE_INLINE_CODE_STRIP.sub("", text)
        text = RE_LINK_STRIP.sub("", text)
        return text
    # Эмодзи
    RE_EMOJI = re.compile(r"[\U0001F300-\U0001FAFF\u2600-\u27BF]")
    # ASCII-диаграмма (НЕ дерево каталогов: дерево начинается с ├── или └── и содержит /)
    ASCII_CHARS = set("┌┐└┘│─├┤┬┴┼")

    # Структура каталога: символ + пробел + slug/
    RE_TREE_NODE = re.compile(r"^\s*(├──|└──|│)\s")

    for i, line, in_fm, in_code in iter_lines_with_context(path):
        if in_fm:
            continue
        # Внутри code fence проверяем только малое подмножество
        if in_code:
            continue

        # Фиксируем начало заголовка (если это не внутри fence)
        h = RE_HEADING.match(line)

        # r-heading-bold: ** в заголовке
        if h and "**" in line:
            rules["heading-bold"].add(path, i, line)

        # r-site-section: ### Baeldung
        if RE_SITE_SEC.match(line):
            rules["site-section"].add(path, i, line)

        # r-backtick-link: [`name`](...)  — только когда это реально markdown-ссылка
        # (за [`...`] идёт "(" — признак markdown-ссылки)
        RE_REAL_BACKTICK_LINK = re.compile(r"\[`[^`\]]+`\]\(")
        if RE_REAL_BACKTICK_LINK.search(line):
            rules["backtick-link"].add(path, i, line)

        # r-md-link-internal: [text](file.md) с относительным путём
        for m in RE_MD_LINK.finditer(line):
            url = m.group(2)
            if url.startswith(("http://", "https://", "/")):
                continue
            # Проверим — ведёт ли в cheatsheets/
            try:
                target = (path.parent / url).resolve()
                if target.is_relative_to(root.resolve()) and target.exists():
                    rules["md-link-internal"].add(path, i, line)
                    break
            except (ValueError, AttributeError):
                pass

        # r-wikilink-broken: [[X]] без существующего file.md
        # Проверяем оригинальную line (wikilinks не бывают внутри inline-кода)
        for m in RE_WIKILINK.finditer(line):
            # Пропускаем, если `[[` внутри inline-кода (между backticks на той же строке до позиции)
            pos = m.start()
            pre = line[:pos]
            # Подсчёт backticks до позиции — если нечётное, мы внутри inline-кода
            if pre.count("`") % 2 == 1:
                continue
            target_name = m.group(1).strip()
            # Игнорируем bash-синтаксис [[ ... ]] (обычно между пробелами)
            if target_name.startswith(".") or target_name == "..." or " " in target_name:
                continue
            if target_name and target_name not in md_index:
                rules["wikilink-broken"].add(path, i, f"[[{target_name}]] — файл не найден")

        # r-bold-parens
        if RE_BOLD_PARENS.search(line):
            rules["bold-parens"].add(path, i, line)

        # r-quotes-ru — проверяем только вне inline-кода
        prose = prose_only(line)
        if RE_QUOTED_RU.search(prose):
            rules["quotes-ru"].add(path, i, line)

        # r-dash-prose — вне inline-кода, вне таблиц, вне markdown-ссылок
        if not line.lstrip().startswith("|") and RE_DASH_PROSE.search(prose):
            rules["dash-prose"].add(path, i, line)

        # r-backtick-bold
        if RE_BT_BOLD.search(line):
            rules["backtick-bold"].add(path, i, line)

        # r-emoji
        if RE_EMOJI.search(line):
            rules["emoji"].add(path, i, line)

        # r-ascii-diagram: ASCII-рамки, если строка содержит символы и НЕ является деревом каталогов
        if any(c in line for c in ASCII_CHARS):
            if RE_TREE_NODE.match(line):
                pass  # дерево, ок
            elif "/" in line and "──" in line:
                pass  # тоже дерево
            else:
                # Это ASCII-рамка — нарушение
                rules["ascii-diagram"].add(path, i, line.strip()[:80])

        # r-trailing-ws
        if line != line.rstrip():
            rules["trailing-ws"].add(path, i, line)


def check_code_blocks(path: Path, rules: dict[str, Rule]):
    """Отдельно: код-блоки без языка."""
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    in_fm = bool(lines and RE_FM.match(lines[0]))
    in_code = False
    for i, line in enumerate(lines, start=1):
        if in_fm and i > 1 and RE_FM.match(line):
            in_fm = False
            continue
        if in_fm:
            continue
        fm = RE_FENCE.match(line)
        if fm:
            indent, rest = fm.group(1), fm.group(2).strip()
            if not in_code:
                if rest == "":
                    rules["code-no-lang"].add(path, i, line)
                in_code = True
            else:
                in_code = False


def build_md_index(root: Path) -> dict[str, Path]:
    idx: dict[str, Path] = {}
    for p in root.rglob("*.md"):
        base = p.stem
        if base not in idx:
            idx[base] = p
    return idx


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("root", type=Path)
    parser.add_argument("--summary", action="store_true")
    parser.add_argument("--rule", type=str, help="Показать нарушения одного правила")
    parser.add_argument("--max-per-rule", type=int, default=20)
    args = parser.parse_args()

    root = args.root
    # CHEATSHEETS_ARCHITECTURE_AND_RULES.md — сам регламент, он содержит примеры
    # запрещённых конструкций в «плохо»-блоках. Исключаем.
    EXCLUDE_NAMES = {"CHEATSHEETS_ARCHITECTURE_AND_RULES.md"}
    files = [
        p for p in sorted(root.rglob("*.md"))
        if "interview" not in p.parts and p.name not in EXCLUDE_NAMES
    ]
    md_index = build_md_index(root)

    rules = {
        "heading-bold": Rule("heading-bold", "Жирный в заголовках"),
        "site-section": Rule("site-section", "Секция названа по сайту (Baeldung/Oracle/...)"),
        "backtick-link": Rule("backtick-link", "Backticks в тексте ссылки [`name`](...)"),
        "md-link-internal": Rule("md-link-internal", "[text](file.md) вместо [[wikilink]]"),
        "wikilink-broken": Rule("wikilink-broken", "[[X]] указывает на несуществующий файл"),
        "bold-parens": Rule("bold-parens", "Жирный внутри скобок (**X**)"),
        "quotes-ru": Rule("quotes-ru", "Прямые \"кавычки\" в русской прозе"),
        "dash-prose": Rule("dash-prose", "Одиночный - вместо длинного — в прозе"),
        "backtick-bold": Rule("backtick-bold", "Комбинация `**X**` или **`X`**"),
        "emoji": Rule("emoji", "Эмодзи / декоративные символы"),
        "ascii-diagram": Rule("ascii-diagram", "ASCII-диаграмма вместо Mermaid"),
        "trailing-ws": Rule("trailing-ws", "Trailing whitespace"),
        "code-no-lang": Rule("code-no-lang", "Код-блок без указания языка"),
    }

    for f in files:
        check_file(f, root, md_index, rules)
        check_code_blocks(f, rules)

    # Отчёт
    total = sum(len(r.violations) for r in rules.values())
    print(f"Scanned: {len(files)} files")
    print(f"Total violations: {total}")
    print()

    # Показать нарушения
    selected = [rules[args.rule]] if args.rule else list(rules.values())
    for rule in selected:
        n = len(rule.violations)
        uniq_files = len({v[0] for v in rule.violations})
        print(f"[{rule.id}] {rule.desc}")
        print(f"  Violations: {n} in {uniq_files} files")
        if not args.summary and rule.violations:
            shown = rule.violations[: args.max_per_rule]
            for p, ln, line in shown:
                rel = p.relative_to(root)
                snippet = line[:120]
                print(f"    {rel}:{ln}: {snippet}")
            if n > args.max_per_rule:
                print(f"    ... and {n - args.max_per_rule} more")
        print()

    sys.exit(0 if total == 0 else 1)


if __name__ == "__main__":
    main()
