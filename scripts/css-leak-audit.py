#!/usr/bin/env python3
"""
css-leak-audit — гейт page-scoped загрузки CSS (Волна 0 дизайна CSS-архитектуры).

Для CSS-файла, который едет не на всех страницах (гейт в css-bundle-manifest.txt),
считает "global-leak": top-level селекторы, которые НЕ привязаны к page-root
`.{page}-page` и потому матчат DOM на других страницах (сломают их, если файл
не грузить, или протекут, если грузить условно). Цель миграции: leak == 0.

Файл с leak > 0 не валит гейт сразу: он может быть заскоуплен не префиксом
селектора, а фактом употребления (гейт `focus,result` у components-answer.css).
Для таких включается вторая, замеряющая, проверка — классы файла ищутся в
разметке за пределами гейта; отчёт помечает такой файл `OK usage`.

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
MANIFEST = Path("modules/quiz-app/src/main/resources/css-bundle-manifest.txt")

# Файл → страницы, на которых он едет.
# Список НЕ держим руками: он выводится из гейтов самого манифеста ленты — того
# файла, по которому CssBundleService собирает /css/bundle/{page}.{hash}.css.
# Причина — ровно тот дрейф, который здесь однажды и случился:
# `pages-focus-confidence.css` подключался условно, но в ручной словарь его не
# внесли, и файл жил без гейта. Пока источник один и тот же, добавить условный
# CSS мимо проверки нельзя.
# (Исторически источником была шапка со списком `th:if="${page == 'X'}"` <link>;
# лента их вытеснила, и гейт вместе с ними умер — теперь он читает ленту.)


def gated_candidates():
    """{имя файла: (page, ...)} по строкам манифеста с гейтом, отличным от `*`."""
    out = {}
    for raw in MANIFEST.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        gate, _, path = line.partition(" ")
        gate, path = gate.strip(), path.strip()
        if not path or gate == "*":
            continue
        out[Path(path).name] = tuple(p for p in gate.split(",") if p)
    return out


CANDIDATES = gated_candidates()

# Некоторые страницы (focus/result) якорят page-scope не через `.{page}-page`,
# а через СЕМАНТИЧЕСКИЕ корни-контейнеры, которые есть только на их DOM.
# Класс/id, чей leftmost-компонент начинается с этих префиксов, считается
# привязанным к странице. ВАЖНО: сюда НЕ входят кросс-страничные контейнеры
# (`.options` — focus+result; `.today-*` — focus+settings; `.question-materials`
# с content-tabs) — они всплывут как unanchored и это правильно: одностраничный
# файл их держать не должен, а многостраничный гейт (`focus,result`) разберёт
# замеряющая проверка по употреблению, где `.options` законны.
EXCLUSIVE_ROOTS = {
    "focus": (".focus-", "#interview", ".training-", ".interview-"),
    "result": (".result-", ".distractor-", ".related-", ".feedback-"),
    "stats": (".stats-", ".chart-", ".topic-table", ".charts-"),
    "settings": (".settings-", ".session-preview", ".builder-", ".filter-card"),
    "summary": (".summary-", ".score-ring"),
}

TEMPLATES_DIR = Path("modules/quiz-app/src/main/resources/templates")

# Второй, замеряющий, режим проверки — для файлов, которые заскоуплены не
# префиксом селектора, а фактом употребления. Такие в ленте есть: гейт
# `focus,result` у components-answer.css законен потому, что ни один из его
# классов не встречается в разметке других страниц, а не потому, что все они
# начинаются с `.result-`. Раньше этот факт жил комментарием в манифесте —
# то есть держался на честном слове и устаревал молча.
#
# Что проверка МОЖЕТ: поймать положительное нарушение — класс из гейтованного
# файла, употреблённый в шаблоне за пределами гейта (ровно тот дрейф, которого
# боимся: «взял .answer на /stats»). Чего НЕ может: доказать отсутствие для
# классов, которые вешает JS (.result-correct, .hljs) — их в разметке нет
# вовсе. Поэтому «нет нарушений» здесь слабее, чем leak=0 по селекторам, и в
# отчёте помечается отдельно (`usage`), а не выдаётся за то же самое.
PAGE_DECL_RE = re.compile(r"page='([a-z-]+)'")
INCLUDE_RE = re.compile(r"th:(?:replace|insert|include)=\"~\{\s*([^:}\s]+)")
CLASS_ATTR_RE = re.compile(r'(?:^|\s)(?:th:)?class(?:append)?="([^"]*)"')
CLASS_TOKEN_RE = re.compile(r"[A-Za-z][A-Za-z0-9_-]*")


def template_includes(path: Path):
    """Шаблоны, которые данный тянет через th:replace/insert/include."""
    out = set()
    for ref in INCLUDE_RE.findall(path.read_text(encoding="utf-8")):
        cand = TEMPLATES_DIR / f"{ref.strip()}.html"
        if cand.exists():
            out.add(cand)
    return out


def page_templates():
    """page → множество шаблонов страницы (входной + транзитивно включённые).

    Значение page объявляет сам шаблон (`th:with="page='focus'"`), поэтому
    соответствие «страница ↔ разметка» тоже не держим руками."""
    incl = {p: template_includes(p) for p in TEMPLATES_DIR.rglob("*.html")}
    pages = {}
    for path, _ in incl.items():
        for page in PAGE_DECL_RE.findall(path.read_text(encoding="utf-8")):
            seen, queue = set(), [path]
            while queue:
                cur = queue.pop()
                if cur in seen:
                    continue
                seen.add(cur)
                queue.extend(incl.get(cur, ()))
            pages.setdefault(page, set()).update(seen)
    return pages


def template_classes(path: Path):
    """Классы, употреблённые в разметке шаблона."""
    out = set()
    for attr in CLASS_ATTR_RE.findall(path.read_text(encoding="utf-8")):
        out.update(CLASS_TOKEN_RE.findall(attr))
    return out


def template_class_groups(path: Path):
    """[set классов] — по одному набору на каждый атрибут class в шаблоне."""
    return [set(CLASS_TOKEN_RE.findall(attr))
            for attr in CLASS_ATTR_RE.findall(path.read_text(encoding="utf-8"))]


def usage_violations(css_path: Path, pages):
    """[(класс, шаблон)] — классы файла, употреблённые ВНЕ его гейта."""
    css = strip_comments(css_path.read_text(encoding="utf-8"))
    own = set(re.findall(r"\.([A-Za-z][A-Za-z0-9_-]*)", css))
    by_page = page_templates()
    allowed = set().union(*(by_page.get(p, set()) for p in pages)) if pages else set()
    bad = []
    for tpl in sorted(TEMPLATES_DIR.rglob("*.html")):
        if tpl in allowed:
            continue
        for cls in sorted(own & template_classes(tpl)):
            bad.append((cls, tpl.relative_to(TEMPLATES_DIR).as_posix()))
    return bad


# ── Обратная проверка: носитель без коробки ──────────────────────────────────
#
# Всё выше смотрит из CSS в разметку: «не выезжает ли селектор за свой гейт».
# Ровно этот вопрос семь итераций подряд оказывался не тем. Дефект, который
# ловит iter-82, устроен зеркально: селектор привязан к странице ЧЕСТНО
# (`.focus-page .recoverable-banner` — leak=0, гейт доволен), а разметку,
# несущую класс, печатает общий фрагмент shell-head.html, то есть все семь
# страниц. На /stats, /bookmarks, /settings и /error баннер «Сессия истекла»
# с role="alert" приезжал `display:block; padding:0; border:0 none;
# background:transparent` — самое важное сообщение приложения рисовалось голым
# абзацем. То же с `.toast` из pages-settings.css, который несёт ещё и
# bookmarks.html.
#
# Инвариант, который здесь проверяется: если класс где-то в корпусе CSS ОПИСАН,
# то на каждой странице, чья разметка его НЕСЁТ, лента этой страницы обязана
# везти файл с его описанием.
#
# Почему условие именно «описан где-то, но не здесь», а не «не описан вовсе»:
# семантических и JS-хуков без единого правила в разметке много и они законны
# (`data-*`-партнёры, якоря для скриптов) — их отсутствие в CSS не дефект.
# Дефект — РАЗНИЦА между страницами: тут коробка есть, там нет. Такая формулировка
# даёт ноль ложных срабатываний на глобальных компонентах (гейт `*` → описан на
# всех страницах) и ловит ровно ту асимметрию, ради которой чек написан.
def styled_classes(css_path: Path):
    """Классы, упомянутые в правилах файла (без комментариев)."""
    return set(re.findall(r"\.([A-Za-z][A-Za-z0-9_-]*)",
                          strip_comments(css_path.read_text(encoding="utf-8"))))


def bundle_gates():
    """[(pages|None, имя файла)] по порядку ленты; None = гейт `*`."""
    out = []
    for raw in MANIFEST.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        gate, _, path = line.partition(" ")
        gate, path = gate.strip(), path.strip()
        if not path:
            continue
        out.append((None if gate == "*" else tuple(p for p in gate.split(",") if p),
                    Path(path).name))
    return out


def unstyled_carriers():
    """[(page, класс, шаблон)] — класс описан в корпусе CSS, разметка страницы
    его несёт, а лента этой страницы файла с описанием не везёт."""
    gates = bundle_gates()
    styled = {}
    for pages, fname in gates:
        p = CSS_DIR / fname
        if not p.exists():
            continue
        styled[fname] = (pages, styled_classes(p))
    anywhere = set().union(*(c for _, c in styled.values())) if styled else set()

    by_page = page_templates()
    bad = []
    for page in sorted(by_page):
        here = set()
        for pages, classes in styled.values():
            if pages is None or page in pages:
                here |= classes
        for tpl in sorted(by_page[page]):
            # Гранулярность — ЭЛЕМЕНТ, а не имя класса. Инвариант звучит «у
            # элемента есть коробка», и элемент с `class="feedback-verdict
            # result-banner"` её имеет: описание даёт партнёрский класс.
            # Единственный носитель — result.html:59, страница `result`;
            # padding/border/radius приезжают из `.result-correct`/`.result-wrong`
            # в components-answer.css, чей гейт `focus,result` эту страницу
            # включает. Если сверять поимённо, такой элемент попадёт в отчёт,
            # хотя коробка у него есть, — и чек начнёт врать.
            for group in template_class_groups(tpl):
                if not group & anywhere or group & here:
                    continue
                for cls in sorted(group & anywhere):
                    bad.append((page, cls, tpl.relative_to(TEMPLATES_DIR).as_posix()))
    return bad


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


def is_anchored(selector: str, pages) -> bool:
    """Привязан ли селектор к странице(ам), на которых едет файл — через
    `.{page}-page` ИЛИ через страничный семантический корень (EXCLUSIVE_ROOTS).

    Гейт бывает многостраничным (`focus,result`): для такого файла привязка к
    ЛЮБОЙ из его страниц законна — за пределы гейта селектор всё равно не
    выезжает, а это и есть проверяемое свойство."""
    core = PREFIX_RE.sub("", selector).strip()
    for page in pages:
        if f".{page}-page" in selector:
            return True
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


def audit(path: Path, pages):
    css = strip_comments(path.read_text(encoding="utf-8"))
    leaks = []
    total_selectors = 0
    for group in iter_rules(css):
        for sel in split_selectors(group):
            total_selectors += 1
            if not is_anchored(sel, pages):
                leaks.append((classify_unanchored(sel), sel.strip()))
    return total_selectors, leaks


def check_syntax(path):
    """Баланс блоков и закрытость комментариев для ОДНОГО файла.

    Зачем отдельно от leak-аудита: leak-аудит регулярочный, он ищет селекторы
    текстом и на сломанном CSS честно отчитывается «0 утечек». Реальный случай:
    лишний `*/` в комментарии обнулил все последующие правила pages-focus.css, а
    гейт показал `249 селекторов, 0 утечек` — поймалось только замером
    computed-стилей в браузере. Второй случай: файл не закрывал свой
    `@layer pages {`; браузер доклеивает `}` в конце, поэтому визуально всё
    работало, но любое следующее правило, дописанное в конец, молча попало бы
    внутрь слоя.

    Возвращает список проблем (строка, описание); пустой список — файл цел.
    """
    src = path.read_text(encoding="utf-8")
    problems = []
    depth = 0
    i = 0
    line = 1
    n = len(src)
    while i < n:
        ch = src[i]
        if ch == "\n":
            line += 1
            i += 1
        elif ch == "/" and i + 1 < n and src[i + 1] == "*":
            end = src.find("*/", i + 2)
            if end < 0:
                problems.append((line, "незакрытый комментарий /*"))
                break
            line += src.count("\n", i, end)
            i = end + 2
        elif ch in "\"'":
            quote = ch
            i += 1
            while i < n and src[i] != quote:
                if src[i] == "\\":
                    i += 1
                elif src[i] == "\n":
                    line += 1
                i += 1
            i += 1
        elif ch == "*" and i + 1 < n and src[i + 1] == "/":
            # `*/` вне комментария: ровно баг, с которого начался этот чек —
            # правка комментария оставила лишний терминатор, весь хвост файла
            # уехал в текст правил, а скобки при этом остались сбалансированы,
            # так что по одному только balance такое не ловится.
            problems.append((line, "лишняя `*/` (комментарий уже закрыт)"))
            i += 2
        else:
            if ch == "{":
                depth += 1
            elif ch == "}":
                depth -= 1
                if depth < 0:
                    problems.append((line, "лишняя `}`"))
                    depth = 0
            i += 1
    if depth > 0:
        problems.append((line, f"не закрыто блоков: {depth}"))
    return problems


def check_layered(path):
    """Есть ли у страничного файла объявление слоя.

    Неслоёные правила по спецификации выигрывают у ЛЮБОГО слоя, поэтому один
    файл без `@layer` играет по другим правилам каскада, чем восемь соседних, —
    и молча перебивает utilities/pixel. Ровно это и случилось с
    pages-bookmarks.css: восемь из девяти были в `@layer pages`, девятый нет,
    и заметить это можно было только чтением исходников подряд.
    """
    if not path.name.startswith("pages-"):
        return []
    src = strip_comments(path.read_text(encoding="utf-8"))
    if re.search(r"@layer\s+[a-z][a-z, ]*\{", src):
        return []
    return [(1, "нет `@layer` — правила выиграют у всех слоёв, в отличие от соседей")]


def audit_syntax_all():
    """Синтаксис проверяем по ВСЕМ css, а не только по page-scoped кандидатам:
    поломка в общем base/components ломает страницы ровно так же."""
    rc = 0
    for path in sorted(CSS_DIR.glob("*.css")):
        for line, msg in check_syntax(path):
            print(f"  [SYNTAX] {path.name}:{line} — {msg}")
            rc = 1
        for line, msg in check_layered(path):
            print(f"  [LAYER] {path.name}:{line} — {msg}")
            rc = 1
    print(f"syntax: {'BROKEN' if rc else 'OK'} "
          f"({len(list(CSS_DIR.glob('*.css')))} файлов)")
    return rc


def main():
    args = sys.argv[1:]
    show_list = "--list" in args
    args = [a for a in args if a != "--list"]

    if args == ["--all"] or not args:
        rc = audit_syntax_all()
        print()
        if not CANDIDATES:
            # Пустой список = разбор разошёлся с форматом манифеста. Молча
            # «0 файлов, 0 утечек» — худший исход для гейта, поэтому валим.
            print(f"  [ERROR] гейтованных строк не найдено в {MANIFEST}")
            return 1
        print(f"{'file':<30} {'page':<14} {'sel':>5} {'leak':>5}")
        print("-" * 60)
        for fname, pages in CANDIDATES.items():
            p = CSS_DIR / fname
            if not p.exists():
                # Манифест ссылается на несуществующий файл — битая строка ленты.
                print(f"  [ERROR] {fname}: есть в манифесте, но файла нет")
                rc = 1
                continue
            total, leaks = audit(p, pages)
            real = [s for k, s in leaks if k == "leak"]
            roots = [s for k, s in leaks if k == "root"]
            if not leaks:
                note = "OK"
            else:
                # Селектор не привязан к странице префиксом — это ещё не приговор:
                # файл может быть заскоуплен употреблением. Проверяем разметкой.
                bad = usage_violations(p, pages)
                note = "OK usage" if not bad else ""
                if bad:
                    rc = 1
                    for cls, tpl in bad[:12]:
                        print(f"  [USAGE] .{cls} употреблён вне гейта: {tpl}")
            print(f"{fname:<30} {','.join(pages):<14} {total:>5} {len(leaks):>5}  {note}"
                  + (f"  (leak={len(real)}, root={len(roots)})" if leaks else ""))

        print()
        orphans = unstyled_carriers()
        for page, cls, tpl in orphans[:20]:
            print(f"  [CARRIER] /{page}: .{cls} несёт {tpl}, "
                  f"а коробку не везёт ни один файл ленты этой страницы")
        print(f"carriers: {'BROKEN' if orphans else 'OK'} "
              f"({len(orphans)} носителей без коробки)")
        if orphans:
            rc = 1
        return rc

    if len(args) < 2:
        print(__doc__)
        return 2
    fname, page = args[0], args[1]
    p = Path(fname)
    if not p.exists():
        p = CSS_DIR / fname
    total, leaks = audit(p, (page,))
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
