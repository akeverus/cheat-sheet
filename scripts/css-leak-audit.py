#!/usr/bin/env python3
"""
css-leak-audit — гейт page-scoped загрузки CSS (Волна 0 дизайна CSS-архитектуры).

Для страничного CSS-файла, который мы хотим грузить условно (th:if page==X),
считает "global-leak": top-level селекторы, которые НЕ привязаны к page-root
`.{page}-page` и потому матчат DOM на других страницах (сломают их, если файл
не грузить, или протекут, если грузить условно). Цель миграции: leak == 0.

Прозрачные обёртки (@layer/@media/@supports/@container) — селекторы внутри них
матчат как обычно. Непрозрачные (@keyframes) — их "селекторы" это стоп-кадры,
не DOM → пропускаем. @font-face/@page — без DOM-селектора → пропускаем.

Нормализация: префиксы html[data-design], [data-theme], [data-pixel=...],
[data-motion=...], [data-js], :root — не меняют "к какой странице привязан"
селектор; проверяем, содержит ли ЛЮБОЙ из comma-селекторов токен `.{page}-page`
(или body-класс страницы). Если нет — это leak.

Usage:
  css-leak-audit.py <file.css> <page>          # аудит одного файла для страницы
  css-leak-audit.py --all                        # аудит всех page-scoped кандидатов
  css-leak-audit.py <file.css> <page> --list     # + перечень каждого leak-селектора

Exit code 0 если leak==0 (для использования как гейт), иначе 1.
"""
import re
import sys
from pathlib import Path

CSS_DIR = Path("modules/quiz-app/src/main/resources/static/css")

# Файл → page-root (значение page-param / body-класс без суффикса -page).
CANDIDATES = {
    "pages-stats.css": "stats",
    "pages-settings.css": "settings",
    "pages-focus.css": "focus",
    "pages-result.css": "result",
    "pages-summary.css": "summary",
    "pages-bookmarks.css": "bookmarks",
    "pages-error.css": "error",
    "pages-login.css": "login",
}

# Некоторые страницы (focus/result) якорят page-scope не через `.{page}-page`,
# а через СЕМАНТИЧЕСКИЕ корни-контейнеры, которые есть только на их DOM.
# Класс/id, чей leftmost-компонент начинается с этих префиксов, считается
# привязанным к странице. ВАЖНО: сюда НЕ входят кросс-страничные контейнеры
# (`.options` — focus+result; `.today-*` — focus+settings; `.question-materials`
# с content-tabs) — они всплывут как unanchored и это правильно (shared-surface
# → выносить в общий компонент, а не scope в один page-файл).
EXCLUSIVE_ROOTS = {
    "focus": (".focus-", "#interview", ".training-", ".interview-"),
    "result": (".result-", ".distractor-", ".related-", ".feedback-"),
    "stats": (".stats-", ".chart-", ".topic-table", ".charts-"),
    "settings": (".settings-", ".session-preview", ".builder-", ".filter-card"),
    "summary": (".summary-", ".score-ring"),
}

# Префиксы, которые не влияют на принадлежность странице (state на <html>).
PREFIX_RE = re.compile(
    r'^(?:html)?(?:\[data-[a-z-]+(?:=(?:"[^"]*"|[^\]]*))?\])*\s*',
)
OPAQUE_AT = ("keyframes", "font-face", "page", "property", "counter-style")
TRANSPARENT_AT = ("media", "supports", "layer", "container", "scope")


def strip_comments(css: str) -> str:
    return re.sub(r"/\*.*?\*/", "", css, flags=re.DOTALL)


def iter_rules(css: str):
    """Yield (selector_text, is_style_rule) для каждого style-правила верхнего
    уровня матчинга (рекурсивно заходя в прозрачные at-rules, пропуская
    непрозрачные)."""
    i, n = 0, len(css)
    while i < n:
        # Найти следующий '{' или конец.
        brace = css.find("{", i)
        if brace == -1:
            break
        prelude = css[i:brace].strip()
        # Тело блока: сматчить парную '}'.
        depth, j = 1, brace + 1
        while j < n and depth:
            c = css[j]
            if c == "{":
                depth += 1
            elif c == "}":
                depth -= 1
            j += 1
        body = css[brace + 1 : j - 1]
        if prelude.startswith("@"):
            at = prelude[1:].split(None, 1)[0].split("(")[0].lower()
            if at in TRANSPARENT_AT:
                yield from iter_rules(body)  # прозрачно — заходим внутрь
            # opaque (keyframes/font-face/...) — пропускаем целиком
        else:
            if prelude:
                yield prelude
            # вложенные правила (CSS nesting) — на всякий случай заходим
            if "{" in body:
                yield from iter_rules(body)
        i = j


def split_selectors(group: str):
    """Разбить comma-группу на отдельные селекторы, уважая () и []."""
    out, depth, cur = [], 0, []
    for ch in group:
        if ch in "([":
            depth += 1
        elif ch in ")]":
            depth -= 1
        if ch == "," and depth == 0:
            out.append("".join(cur).strip())
            cur = []
        else:
            cur.append(ch)
    if cur:
        out.append("".join(cur).strip())
    return [s for s in out if s]


def is_anchored(selector: str, page: str) -> bool:
    """Привязан ли селектор к своей странице — через `.{page}-page` ИЛИ через
    страничный семантический корень (EXCLUSIVE_ROOTS)."""
    if f".{page}-page" in selector:
        return True
    core = PREFIX_RE.sub("", selector).strip()
    for pref in EXCLUSIVE_ROOTS.get(page, ()):  # leftmost-компонент
        if core.startswith(pref):
            return True
    return False


def classify_unanchored(selector: str) -> str:
    """global-root (матчит документ: html/body/:root) vs leak (класс/элемент,
    матчащий широко на других страницах)."""
    core = PREFIX_RE.sub("", selector).strip()
    if core == "" or re.match(r"^(:root|html|body)\b", core):
        return "root"
    return "leak"


def audit(path: Path, page: str):
    css = strip_comments(path.read_text(encoding="utf-8"))
    leaks = []
    total_selectors = 0
    for group in iter_rules(css):
        for sel in split_selectors(group):
            total_selectors += 1
            if not is_anchored(sel, page):
                leaks.append((classify_unanchored(sel), sel.strip()))
    return total_selectors, leaks


def main():
    args = sys.argv[1:]
    show_list = "--list" in args
    args = [a for a in args if a != "--list"]

    if args == ["--all"] or not args:
        rc = 0
        print(f"{'file':<24} {'page':<10} {'sel':>5} {'leak':>5}")
        print("-" * 50)
        for fname, page in CANDIDATES.items():
            p = CSS_DIR / fname
            if not p.exists():
                continue
            total, leaks = audit(p, page)
            real = [s for k, s in leaks if k == "leak"]
            roots = [s for k, s in leaks if k == "root"]
            flag = "OK" if not leaks else ""
            print(f"{fname:<24} {page:<10} {total:>5} {len(leaks):>5}  {flag}"
                  + (f"  (leak={len(real)}, root={len(roots)})" if leaks else ""))
            if leaks:
                rc = 1
        return rc

    if len(args) < 2:
        print(__doc__)
        return 2
    fname, page = args[0], args[1]
    p = Path(fname)
    if not p.exists():
        p = CSS_DIR / fname
    total, leaks = audit(p, page)
    real = [s for k, s in leaks if k == "leak"]
    roots = [s for k, s in leaks if k == "root"]
    print(f"{p.name}: {total} селекторов, {len(leaks)} leak "
          f"(leak={len(real)}, root/html={len(roots)}) — page-root .{page}-page")
    if show_list:
        for kind, sel in leaks:
            tag = "ROOT" if kind == "root" else "LEAK"
            print(f"  [{tag}] {sel[:110]}")
    return 0 if not leaks else 1


if __name__ == "__main__":
    sys.exit(main())
