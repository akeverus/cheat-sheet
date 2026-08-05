#!/usr/bin/env python3
"""Исходник спрайт-листов кадровой анимации (static/img/animations/sheets/*.png).

PNG и метаданные — производные артефакты, источник правды здесь. Править надо
этот файл и перегенерировать, а не пиксели в редакторе.

Лист ровно один, и это осмысленное состояние, а не недоделка. Набор успел
разрастись до 21 листа при том, что атрибут `data-sprite-anim` код выставляет
для одного имени — `incorrect-subtle` в `pixel-fx.js`. Остальные двадцать были
мертвы на уровень выше, чем умеет заметить инвентарь ассетов: CSS-правило
ссылалось на файл через `url()`, поэтому «ссылка есть» — а селектор правила не
совпадал ни с одним элементом за всё время жизни приложения. Такой мёртвый вес
не падает, не мигает и не виден в UI; он обнаруживается только сверкой «правила
против кода», которую держит `SpriteAnimationContractTest`.

Палитра **читается из `static/css/tokens.css` по имени токена**, а не хранится
здесь копией HEX. Копия уже подводила: лист жил на почти-совпадающей палитре
(плитка `#10242d` против токенной `#071116`, красный `#fb5f5f` против
`#ff715f`). Для этого спрайта расхождение особенно заметно — он всплывает
поверх экрана вопроса в момент ответа, то есть гарантированно попадает в один
кадр с интерфейсом и читается не как решение дизайнера, а как баг рендера.

Запуск:  python3 scripts/gen-sprite-sheets.py
"""

import json
import re
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
STATIC = ROOT / "modules/quiz-app/src/main/resources/static"
SHEETS = STATIC / "img/animations/sheets"
METADATA = STATIC / "img/animations/metadata"
TOKENS = STATIC / "css/tokens.css"

# --- палитра -----------------------------------------------------------------
# Две роли: K — непрозрачная плитка, E — рамка и глиф «неверно».
#
# Плитка непрозрачна намеренно: спрайт всплывает и над тёмной, и над светлой
# темой, поэтому свободный силуэт пришлось бы делать в двух вариантах. Берётся
# тёмная ветка токенов — она в файле первая, светлая переопределяет её ниже.
# Та же роль и тот же токен, что у плитки опознавательных знаков разделов:
# два пиксельных семейства продукта стоят на одной подложке не случайно.
ROLES = {
    "K": "--color-bg-primary",
    "E": "--color-status-error",
}


def read_palette():
    css = TOKENS.read_text(encoding="utf-8")
    palette = {}
    for role, token in ROLES.items():
        match = re.search(re.escape(token) + r":\s*(#[0-9a-fA-F]{6})\b", css)
        if not match:
            raise SystemExit(f"в tokens.css нет {token}")
        value = match.group(1)
        palette[role] = tuple(int(value[i:i + 2], 16) for i in (1, 3, 5))
    return palette


PALETTE = read_palette()

FRAME = 64
CENTER = FRAME // 2

# --- геометрия кадра ---------------------------------------------------------
# Плитка 41×41 с рамкой 3px и крестом внутри. Крест — пересечение двух полос
# толщиной 5 (|u−v| ≤ 2 и |u+v| ≤ 2) с диском радиуса 13: диск даёт скошенные
# концы, поэтому крест не упирается в рамку торцами.
PLATE = 41
BORDER = 3
ARM = 2
RADIUS_SQ = 169

# Кадровка: короткий горизонтальный сбой на пять кадров и пять кадров покоя.
# Промах — не наказание (§10 мастер-плана), поэтому вместо тряски всей карточки
# спрайт лишь однократно вздрагивает сам и гаснет.
SHAKE = (0, 1, -1, 1, -1, 0, 0, 0, 0, 0)
DURATION_MS = 70


def frame(dx):
    """Маска одного кадра: None — прозрачно, иначе роль палитры."""
    px = [[None] * FRAME for _ in range(FRAME)]
    x0 = CENTER - PLATE // 2 + dx
    y0 = CENTER - PLATE // 2
    for row in range(PLATE):
        for col in range(PLATE):
            edge = min(row, col, PLATE - 1 - row, PLATE - 1 - col) < BORDER
            px[y0 + row][x0 + col] = "E" if edge else "K"

    for y in range(FRAME):
        for x in range(FRAME):
            u = x - CENTER - dx
            v = y - CENTER
            if u * u + v * v > RADIUS_SQ:
                continue
            if abs(u - v) <= ARM or abs(u + v) <= ARM:
                px[y][x] = "E"
    return px


def sheet(frames):
    """Горизонтальный лист: кадры встык, слева направо."""
    width = FRAME * len(frames)
    rows = [[None] * width for _ in range(FRAME)]
    for index, px in enumerate(frames):
        for y in range(FRAME):
            for x in range(FRAME):
                rows[y][index * FRAME + x] = px[y][x]
    return rows


def write_png(path, rows):
    height = len(rows)
    width = len(rows[0])
    raw = bytearray()
    for y in range(height):
        raw.append(0)
        for x in range(width):
            role = rows[y][x]
            if role is None:
                raw += b"\x00\x00\x00\x00"
            else:
                r, g, b = PALETTE[role]
                raw += bytes((r, g, b, 255))

    def chunk(kind, data):
        body = kind + data
        return (
            struct.pack(">I", len(data))
            + body
            + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)
        )

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    png += chunk(b"IEND", b"")
    path.write_bytes(png)


def main():
    name = "incorrect-subtle"
    frames = [frame(dx) for dx in SHAKE]
    write_png(SHEETS / f"{name}-sheet.png", sheet(frames))
    (METADATA / f"{name}.json").write_text(
        json.dumps(
            {
                "name": name,
                "frames": len(frames),
                "frameWidth": FRAME,
                "frameHeight": FRAME,
                "durationMs": DURATION_MS,
                "layout": "horizontal",
                "loop": 0,
            },
            ensure_ascii=False,
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    print(f"{name}: {len(frames)} кадров {FRAME}×{FRAME}")


if __name__ == "__main__":
    main()
