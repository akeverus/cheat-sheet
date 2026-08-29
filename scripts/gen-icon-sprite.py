#!/usr/bin/env python3
"""Генератор SVG-спрайтов интерфейса из Lucide (ISC).

Направление FOLIO (миссия 2026-08-29): один линейный язык иконок на весь
продукт — Lucide, сетка 24×24, stroke=currentColor (задаёт .ui-icon в
components-icons.css). Заменил пиксельный rect-спрайт 16×16 гильдейского
направления.

Источник геометрии — пакет lucide-static (npm), от него берётся ВНУТРЕННОСТЬ
каждого SVG без presentation-атрибутов обёртки: цвет и толщину штриха задаёт
потребительский класс, поэтому иконки наследуют цвет текста и одинаково живут
в обеих темах.

Запуск (из корня репо):
    npm install --no-save lucide-static   # или указать путь пакету
    python3 scripts/gen-icon-sprite.py [путь/к/lucide-static]

Выход: templates/fragments/icons.html и state-illustrations.html.
Карта id→имя ниже — единственный источник правды состава спрайтов;
контракт стережёт IconLanguageContractTest.
"""
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
RES = REPO / "modules/quiz-app/src/main/resources"

LUCIDE = Path(sys.argv[1]) if len(sys.argv) > 1 else REPO / "node_modules/lucide-static"

# ---- состав спрайта интерфейсных иконок (fragments/icons.html) --------------
ICONS = {
    # навигация и глобальные действия
    "training":  "pencil",
    "topics":    "library-big",
    "analytics": "chart-column",
    "bookmark":  "bookmark",
    "settings":  "sliders-horizontal",
    "theme":     "sun-moon",
    "logout":    "log-out",
    "arrow-up":  "arrow-up",
    # действия и статусы при тексте
    "star":         "star",
    "flame":        "flame",
    "flag":         "flag",
    "target":       "target",
    "download":     "download",
    "printer":      "printer",
    "copy":         "copy",
    "check":        "check",
    "x":            "x",
    "circle-check": "circle-check",
    "circle-x":     "circle-x",
    "code":         "code-xml",
    "diagram":      "workflow",
    "link":         "link",
    "book-open":    "book-open",
    # бренд-знак
    "crest": "book-marked",
    # знаки отличия (id = Achievement.id())
    "badge-first-step":  "footprints",
    "badge-hundred":     "medal",
    "badge-steady-hand": "gauge",
    "badge-week":        "calendar-check",
    "badge-mastery":     "award",
    "badge-month":       "calendar-days",
    # ступени прогрессии (id = RankUtils.glyph())
    "rank-apprentice":    "sprout",
    "rank-journeyman":    "map",
    "rank-craftsman":     "hammer",
    "rank-master":        "gem",
    "rank-senior-master": "crown",
    "rank-keeper":        "shield",
    "rank-magister":      "graduation-cap",
    # знаки разделов каталога (id = topic-groups.yaml icon)
    "section-foundation":   "blocks",
    "section-languages":    "braces",
    "section-jvm":          "coffee",
    "section-spring":       "leaf",
    "section-databases":    "database",
    "section-architecture": "network",
    "section-backend":      "server",
    "section-delivery":     "rocket",
    "section-engineering":  "wrench",
    "section-misc":         "shapes",
    # режимы сессии (id = ModeUtils icon)
    "mode-training":  "pencil-line",
    "mode-study":     "book-open-text",
    "mode-flashcard": "layers",
    "mode-typed":     "keyboard",
    "mode-interview": "mic",
    "mode-forge":     "square-terminal",
    "mode-exam":      "timer",
    "mode-path":      "route",
}

# ---- состав спрайта состояний (fragments/state-illustrations.html) ----------
STATES = {
    # глифы пустых состояний (использование: .ui-illus)
    "state-offline":       "wifi-off",
    "state-diagram-error": "image-off",
    "state-shelf":         "library",
    "state-chronicle":     "scroll-text",
    "state-clean-slate":   "sparkles",
    "state-locked":        "lock",
    "state-search":        "search",
    # сцены (декоративные hero-глифы: превью настроек, печать итогов)
    "scene-lost-map":          "compass",
    "scene-shelf":             "book-copy",
    "scene-desk":              "notebook-pen",
    "scene-chronicle-sealed":  "file-lock",
}

SVG_OPEN = re.compile(r"<svg\b[^>]*>", re.S)


def lucide_body(name: str) -> str:
    path = LUCIDE / "icons" / f"{name}.svg"
    raw = path.read_text(encoding="utf-8")
    body = SVG_OPEN.sub("", raw, count=1).replace("</svg>", "")
    # lucide-static кладёт лицензионный комментарий внутрь — он уедет в шапку файла
    body = re.sub(r"<!--.*?-->", "", body, flags=re.S)
    return " ".join(body.split())


def build(mapping: dict, title: str, usage: str) -> str:
    symbols = "\n".join(
        f'    <symbol id="{sid}" viewBox="0 0 24 24">{lucide_body(name)}</symbol>'
        for sid, name in mapping.items()
    )
    return f"""<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/*
  {title}

  Язык: Lucide (https://lucide.dev, лицензия ISC), сетка 24×24, линия 2px.
  Символы БЕЗ presentation-атрибутов: stroke/fill задаёт потребительский класс
  через currentColor, поэтому глиф наследует цвет текста и одинаково живёт в
  обеих темах. Файл СГЕНЕРИРОВАН scripts/gen-icon-sprite.py — правки руками
  перетираются; состав меняется в карте генератора.

  Использование: {usage}
*/-->
<svg th:fragment="sprite" class="icon-sprite" xmlns="http://www.w3.org/2000/svg" aria-hidden="true" focusable="false">
{symbols}
</svg>
</body>
</html>
"""


def main() -> None:
    icons = build(
        ICONS,
        "СПРАЙТ ИНТЕРФЕЙСНЫХ ИКОНОК — ОДИН ЯЗЫК НА ВЕСЬ ПРОДУКТ.",
        '<svg class="ui-icon" aria-hidden="true"><use href="#name"></use></svg>',
    )
    states = build(
        STATES,
        "СПРАЙТ ГЛИФОВ СОСТОЯНИЙ — пустые состояния и декоративные сцены.",
        '<svg class="ui-illus" aria-hidden="true"><use href="#state-…"></use></svg>',
    )
    (RES / "templates/fragments/icons.html").write_text(icons, encoding="utf-8")
    (RES / "templates/fragments/state-illustrations.html").write_text(states, encoding="utf-8")
    print(f"icons.html: {len(ICONS)} символов; state-illustrations.html: {len(STATES)} символов")


if __name__ == "__main__":
    main()
