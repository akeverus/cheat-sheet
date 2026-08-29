#!/usr/bin/env python3
"""
check-arch-anchors — валидатор якорей архитектурного документа
(`docs/backend-architecture.md` и его наследники, CS-20260828-006).

Три независимые проверки, все — по документу целиком:

  A. Якоря `` `<prefix>:<path>:<line>` `` — префикс известен, файл существует,
     номер строки попадает в границы файла.
  B. Классоподобные токены в бэктиках (`[A-Z][A-Za-z0-9]{3,}`), не входящие в
     якорь, — резолвятся в basename существующего `*.java` или лежат в ALLOWLIST.
  C. Каждый строковый литерал пути из `@GetMapping`/`@PostMapping`/`@PutMapping`/
     `@DeleteMapping`/`@RequestMapping` в `modules/quiz-app/src/main/java`
     встречается в документе дословно. Аннотации без литерала (именованные
     параметры, конкатенация) не участвуют — они документируются прозой.

Usage:
  check-arch-anchors.py [path/to/doc.md]   # дефолт: docs/backend-architecture.md

Exit 0 и печать `anchors=N ok, symbols=M ok, mappings=K ok` при полном успехе;
exit 1 и печать всех нарушений — при любом провале хотя бы одной проверки.
"""

import re
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent

PREFIXES = {
    "app": "modules/quiz-app/src/main/java/com/cheatsheet/quiz/",
    "dom": "modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/",
    "per": "modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/",
    "mig": "modules/quiz-persistence/src/main/resources/db/migration/",
    "res": "modules/quiz-app/src/main/resources/",
    "tst": "modules/quiz-app/src/test/java/com/cheatsheet/quiz/",
}

# Токены, не резолвящиеся в *.java, но легитимные в прозе документа: ключевые
# слова SQL, типы JDK/Spring, константы перечислений, вложенные записи, внешние
# имена. Новый неизвестный токен обязан падать громко — не расширять втихую.
ALLOWLIST = {
    # SQL
    "SELECT", "WHERE", "CASE", "COALESCE", "ORDER", "ELSE", "NULL", "UPDATE", "DESC",
    # JDK / Spring
    "ProblemDetail", "Jsoup", "ConcurrentHashMap", "HttpServletRequest", "HttpSession",
    "IllegalStateException", "NoResourceFoundException", "ResponseStatusException",
    "AccessDeniedHandler", "AuthenticationEntryPoint", "PostMapping", "RequestMapping",
    # константы перечислений
    "OVERDUE", "BOOKMARK", "EXAM", "MERMAID", "WRONG", "TYPO", "OTHER", "STUDY",
    "MARATHON", "TRAINING", "MISTAKES", "TOPIC", "REDERIVED",
    # вложенные записи/типы
    "SurfaceState", "NextAction", "Destination", "ModeOption",
    # внешние имена
    "CockroachDB", "Referer", "Accept",
    # HTTP-заголовки и Spring-аннотации, упомянутые прозой
    "Content", "Type", "Retry", "After", "ControllerAdvice", "Admin", "Token",
    "POST", "Idempotency",
    # glob-паттерн `*Repository*`
    "Repository",
}

ANCHOR_RE = re.compile(r"`(app|dom|per|mig|res|tst):([^`]+):(\d+)`")
BACKTICK_RE = re.compile(r"`([^`]+)`")
CLASSLIKE_RE = re.compile(r"\b[A-Z][A-Za-z0-9]{3,}\b")
FENCE_RE = re.compile(r"```.*?```", re.DOTALL)
MAPPING_RE = re.compile(
    r'@(?:Get|Post|Put|Delete|Request)Mapping\(\s*"([^"]+)"\s*\)'
)


def check_anchors(text: str) -> tuple[list[str], int]:
    errors = []
    ok = 0
    for m in ANCHOR_RE.finditer(text):
        prefix, rel_path, line_s = m.group(1), m.group(2), m.group(3)
        full = REPO_ROOT / PREFIXES[prefix] / rel_path
        if not full.is_file():
            errors.append(f"A: якорь `{m.group(0)}` — файл не существует: {full}")
            continue
        line = int(line_s)
        total = sum(1 for _ in full.open(encoding="utf-8"))
        if line < 1 or line > total:
            errors.append(
                f"A: якорь `{m.group(0)}` — строка {line} вне диапазона 1..{total}"
            )
            continue
        ok += 1
    return errors, ok


def build_java_basenames() -> set[str]:
    return {p.stem for p in (REPO_ROOT / "modules").rglob("*.java")}


def check_symbols(text: str, java_basenames: set[str]) -> tuple[list[str], int]:
    errors = []
    seen: dict[str, bool] = {}
    # Тройные бэктики (fenced code blocks, ```bash ... ```) — не инлайн-код,
    # BACKTICK_RE не должен трактовать их содержимое как классоподобные токены.
    text = FENCE_RE.sub("", text)
    for bm in BACKTICK_RE.finditer(text):
        span = bm.group(0)
        if ANCHOR_RE.fullmatch(span):
            continue
        content = bm.group(1)
        for cm in CLASSLIKE_RE.finditer(content):
            token = cm.group(0)
            if token in seen:
                continue
            seen[token] = token in java_basenames or token in ALLOWLIST
    ok = 0
    for token, valid in seen.items():
        if valid:
            ok += 1
        else:
            errors.append(
                f"B: неизвестный токен `{token}` — не резолвится в *.java и не в allowlist"
            )
    return errors, ok


def collect_mappings() -> set[str]:
    paths = set()
    for f in (REPO_ROOT / "modules/quiz-app/src/main/java").rglob("*.java"):
        content = f.read_text(encoding="utf-8")
        for m in MAPPING_RE.finditer(content):
            paths.add(m.group(1))
    return paths


def check_mappings(text: str) -> tuple[list[str], int]:
    errors = []
    ok = 0
    for path in sorted(collect_mappings()):
        if path in text:
            ok += 1
        else:
            errors.append(f"C: путь `{path}` из @*Mapping отсутствует в документе дословно")
    return errors, ok


def main() -> int:
    doc_path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("docs/backend-architecture.md")
    if not doc_path.is_absolute():
        doc_path = REPO_ROOT / doc_path
    if not doc_path.is_file():
        print(f"ошибка: документ не найден: {doc_path}", file=sys.stderr)
        return 1

    text = doc_path.read_text(encoding="utf-8")

    anchor_errors, anchor_ok = check_anchors(text)
    symbol_errors, symbol_ok = check_symbols(text, build_java_basenames())
    mapping_errors, mapping_ok = check_mappings(text)

    all_errors = anchor_errors + symbol_errors + mapping_errors
    for e in all_errors:
        print(e)

    print(f"anchors={anchor_ok} ok, symbols={symbol_ok} ok, mappings={mapping_ok} ok")

    return 1 if all_errors else 0


if __name__ == "__main__":
    sys.exit(main())
