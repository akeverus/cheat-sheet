#!/usr/bin/env python3
"""Исходник опознавательных знаков разделов (static/img/stickers/*.png).

PNG — производный артефакт, источник правды здесь. Если знак нужно поправить,
правим этот файл и перегенерируем, а не редактируем пиксели в редакторе.

Единый приём для всех десяти знаков: тёмная плитка с бирюзовой рамкой, глиф
внутри. Плитка нужна, потому что знаки лежат на белой карточке раздела в светлой
теме и на тёмной поверхности в тёмной — свободный силуэт со светлыми заливками
пропадал бы на белом. Тёмная плитка даёт постоянный контекст обеим темам.

Палитра не задаётся здесь, а **читается из `static/css/tokens.css` по имени
токена** — шесть ролей, шесть токенов. Своя копия HEX уже подводила: семейство
знаков жило на почти-совпадающей палитре (`#22d3c5` против токенного `#39d0c7`
и так далее — разница ~10 единиц в канале). Такое расхождение видно только когда
знак и интерфейс попадают в один экран, и читается не как решение дизайнера, а
как баг рендера. Привязка по имени делает расхождение невозможным: сменится
токен — сменится и знак. Тест `SectionStickerPaletteTest` читает те же токены.

Запуск:  python3 scripts/gen-section-stickers.py
"""

import re
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "modules/quiz-app/src/main/resources/static/img/stickers"
TOKENS = ROOT / "modules/quiz-app/src/main/resources/static/css/tokens.css"

# --- палитра -----------------------------------------------------------------
# Роли, а не «цвета»: K — плитка, S — вторичная поверхность, T — рамка и акцент,
# B — холодный вторичный акцент, G — «готово/запуск», W — свет.
#
# Знаки одинаковы в обеих темах (плитка непрозрачная), поэтому берётся тёмная
# ветка токенов — она в файле первая, светлая переопределяет её ниже.
ROLES = {
    "K": "--color-bg-primary",
    "S": "--color-border-primary",
    "T": "--color-accent-primary",
    "B": "--color-status-info",
    "G": "--color-status-success",
    "W": "--color-text-primary",
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

TILE = 32  # логическая сетка; итоговый PNG — 64×64 (масштаб ×2)
SCALE = 2
GLYPH = 24  # площадка глифа внутри плитки
OFFSET = (TILE - GLYPH) // 2


class Canvas:
    def __init__(self, size):
        self.size = size
        self.px = [[None] * size for _ in range(size)]

    def set(self, x, y, c):
        if 0 <= x < self.size and 0 <= y < self.size and c is not None:
            self.px[y][x] = c

    def rect(self, x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.set(x, y, c)

    def disc(self, cx, cy, r, c):
        for y in range(int(cy - r) - 1, int(cy + r) + 2):
            for x in range(int(cx - r) - 1, int(cx + r) + 2):
                if (x - cx) ** 2 + (y - cy) ** 2 <= r * r:
                    self.set(x, y, c)

    def blit(self, art, ox, oy):
        for y, row in enumerate(art):
            for x, ch in enumerate(row):
                if ch != ".":
                    self.set(ox + x, oy + y, ch)


def tile():
    """Тёмная плитка с бирюзовой рамкой и срезанными по пикселю углами."""
    c = Canvas(TILE)
    c.rect(0, 0, TILE - 1, TILE - 1, "T")
    c.rect(1, 1, TILE - 2, TILE - 2, "K")
    for x, y in ((0, 0), (TILE - 1, 0), (0, TILE - 1), (TILE - 1, TILE - 1)):
        c.px[y][x] = None
    for x, y in ((1, 1), (TILE - 2, 1), (1, TILE - 2), (TILE - 2, TILE - 2)):
        c.px[y][x] = "T"
    return c


def art(c, rows, ox=OFFSET, oy=OFFSET):
    c.blit(rows, ox, oy)
    return c


# --- глифы -------------------------------------------------------------------
# Каждая строка ровно 24 символа; '.' — прозрачно (виден фон плитки).

BOOK = [
    "........................",
    "........................",
    "........................",
    "........................",
    "...BBBBBBBB..TTTTTTTT...",
    "..BBBBBBBBB..TTTTTTTTT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "..BKKKKKKBBWWTTKKKKKKT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "..BKKKKKKBBWWTTKKKKKKT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "..BKKKKKKBBWWTTKKKKKKT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "..BKKKKKKBBWWTTKKKKKKT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "..BBBBBBBBBWWTTTTTTTTT..",
    "...BBBBBBBB..TTTTTTTT...",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
]

CODE = [
    "........................",
    "........................",
    "........................",
    "........................",
    "......BB........BB......",
    ".....BB....TT....BB.....",
    "....BB.....TT.....BB....",
    "...BB.....TT.......BB...",
    "..BB......TT........BB..",
    ".BB......TT..........BB.",
    ".BB......TT..........BB.",
    "..BB....TT..........BB..",
    "...BB...TT.........BB...",
    "....BB.TT.........BB....",
    ".....BBTT........BB.....",
    "......BB........BB......",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
    "........................",
]

COFFEE = [
    "........................",
    "..T....T....T...........",
    "..T....T....T...........",
    "...T....T....T..........",
    "...T....T....T..........",
    "..T....T....T...........",
    "..T....T....T...........",
    "........................",
    "..WWWWWWWWWWWW..........",
    "..WWWWWWWWWWWW..TTTT....",
    "..WWWWWWWWWWWW.TT..TT...",
    "..WWWWWWWWWWWW.TT..TT...",
    "..WWWWWWWWWWWW.TT..TT...",
    "..WWWWWWWWWWWW..TTTT....",
    "..WWWWWWWWWWWW..........",
    "...WWWWWWWWWW...........",
    "....WWWWWWWW............",
    "........................",
    ".TTTTTTTTTTTTTTTTTT.....",
    ".TTTTTTTTTTTTTTTTTT.....",
    "........................",
    "........................",
    "........................",
    "........................",
]

ROCKET = [
    "........................",
    "..........WW............",
    ".........WWWW...........",
    "........WWWWWW..........",
    "........WWWWWW..........",
    ".......WWWWWWWW.........",
    ".......WWWBBWWW.........",
    ".......WWBBBBWW.........",
    ".......WWBBBBWW.........",
    ".......WWWBBWWW.........",
    ".......WWWWWWWW.........",
    "......TWWWWWWWWT........",
    ".....TTWWWWWWWWTT.......",
    "....TTTWWWWWWWWTTT......",
    "...TTTTWWWWWWWWTTTT.....",
    "...TTT.WWWWWWWW.TTT.....",
    "...TT..WWWWWWWW..TT.....",
    "........GGGGGG..........",
    ".........GGGG...........",
    "..........GG............",
    "........................",
    "........................",
    "........................",
    "........................",
]

NETWORK = [
    "........................",
    "..........TTT...........",
    ".........TTTTT..........",
    ".........TTTTT..........",
    "..........TTT...........",
    "........BB...BB.........",
    ".......BB.....BB........",
    "......BB.......BB.......",
    ".....BB.........BB......",
    "...TTT.....G.....TTT....",
    "..TTTTT...GGG...TTTTT...",
    "..TTTTT..GGGGG..TTTTT...",
    "..TTTTT...GGG...TTTTT...",
    "...TTT.....G.....TTT....",
    ".....BB.........BB......",
    "......BB.......BB.......",
    ".......BB.....BB........",
    "........BB...BB.........",
    "..........TTT...........",
    ".........TTTTT..........",
    ".........TTTTT..........",
    "..........TTT...........",
    "........................",
    "........................",
]

# Три отдельные «шайбы» читались стопкой полос — тем же силуэтом, что бургер-меню
# или «слои». Цилиндр опознаётся не по количеству ярусов, а по эллипсу: верхняя
# крышка светлее корпуса, а швы прогибаются вниз к центру. Швы рисуются цветом
# плитки (K), поэтому силуэт остаётся сплошным — контур не рвётся по краям.
DATABASE = [
    "........................",
    "........................",
    "........................",
    "......BBBBBBBBBBBB......",
    "....BBBBBBBBBBBBBBBB....",
    "...BBBBBBBBBBBBBBBBBB...",
    "...TTBBBBBBBBBBBBBBTT...",
    "...TTTTBBBBBBBBBBTTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TKKTTTTTTTTTTTTKKT...",
    "...TTTKKKKKKKKKKKKTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TKKTTTTTTTTTTTTKKT...",
    "...TTTKKKKKKKKKKKKTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "...TTTTTTTTTTTTTTTTTT...",
    "....TTTTTTTTTTTTTTTT....",
    "......TTTTTTTTTTTT......",
    "........................",
    "........................",
    "........................",
]

TERMINAL = [
    "........................",
    "........................",
    "..SSSSSSSSSSSSSSSSSS....",
    "..SSSSSSSSSSSSSSSSSS....",
    "..SSTT..TT..TT..SSSS....",
    "..SSSSSSSSSSSSSSSSSS....",
    "..KKKKKKKKKKKKKKKKKK....",
    "..KKKKKKKKKKKKKKKKKK....",
    "..KKBB..KKKKKKKKKKKK....",
    "..KKKBB.KKKKKKKKKKKK....",
    "..KKKKBBKKKKKKKKKKKK....",
    "..KKKKKBBKKKKKKKKKKK....",
    "..KKKKBBKKKKKKKKKKKK....",
    "..KKKBB.KKKKKKKKKKKK....",
    "..KKBB..KKKKKKKKKKKK....",
    "..KKKKKKKKKKKKKKKKKK....",
    "..KKKKKGGGGGGGKKKKKK....",
    "..KKKKKGGGGGGGKKKKKK....",
    "..KKKKKKKKKKKKKKKKKK....",
    "..KKKKKKKKKKKKKKKKKK....",
    "........................",
    "........................",
    "........................",
    "........................",
]

CLOUD = [
    "........................",
    "........................",
    "........................",
    ".........BBBB...........",
    ".......BBBBBBBB.........",
    "......BBBBBBBBBB........",
    "....BBBBBBBBBBBBBB......",
    "...BBBBBBBBBBBBBBBB.....",
    "..BBBBBBBBBBBBBBBBBB....",
    "..BBBBBBBBBBBBBBBBBB....",
    "..BBBBBBBBBBBBBBBBBB....",
    "...BBBBBBBBBBBBBBBB.....",
    "........................",
    ".........WWWW...........",
    "........WWWWWW..........",
    ".......WWWWWWWW.........",
    "......WWWWWWWWWW........",
    ".........WWWW...........",
    ".........WWWW...........",
    ".........WWWW...........",
    "........................",
    "........................",
    "........................",
    "........................",
]

QUESTION = [
    "........................",
    "........................",
    "......TTTTTTTTTT........",
    ".....TTTTTTTTTTTT.......",
    "....TTTT......TTTT......",
    "....TTT........TTT......",
    "...............TTT......",
    "..............TTTT......",
    "............TTTTT.......",
    ".........TTTTTTT........",
    ".......TTTTTTT..........",
    ".......TTTT.............",
    ".......TTT..............",
    ".......TTT..............",
    "........................",
    "........................",
    ".......BBB..............",
    "......BBBBB.............",
    "......BBBBB.............",
    ".......BBB..............",
    "........................",
    "........................",
    "........................",
    "........................",
]


def target():
    c = tile()
    cx = cy = 15.5
    c.disc(cx, cy, 11.0, "T")
    c.disc(cx, cy, 8.2, "K")
    c.disc(cx, cy, 5.5, "B")
    c.disc(cx, cy, 2.8, "K")
    c.disc(cx, cy, 1.4, "W")
    return c


ICONS = {
    "book.png": lambda: art(tile(), BOOK),
    "code.png": lambda: art(tile(), CODE),
    "coffee.png": lambda: art(tile(), COFFEE),
    "rocket.png": lambda: art(tile(), ROCKET),
    "database.png": lambda: art(tile(), DATABASE),
    "network.png": lambda: art(tile(), NETWORK),
    "terminal.png": lambda: art(tile(), TERMINAL),
    "cloud-upload.png": lambda: art(tile(), CLOUD),
    "target.png": target,
    "question.png": lambda: art(tile(), QUESTION),
}


def write_png(path, canvas):
    size = canvas.size * SCALE
    raw = bytearray()
    for y in range(size):
        raw.append(0)
        for x in range(size):
            ch = canvas.px[y // SCALE][x // SCALE]
            if ch is None:
                raw += b"\x00\x00\x00\x00"
            else:
                r, g, b = PALETTE[ch]
                raw += bytes((r, g, b, 255))

    def chunk(kind, data):
        body = kind + data
        return (
            struct.pack(">I", len(data))
            + body
            + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)
        )

    png = (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", struct.pack(">IIBBBBB", size, size, 8, 6, 0, 0, 0))
        + chunk(b"IDAT", zlib.compress(bytes(raw), 9))
        + chunk(b"IEND", b"")
    )
    path.write_bytes(png)


def main():
    for name, build in ICONS.items():
        write_png(OUT / name, build())
    print(f"generated {len(ICONS)} stickers -> {OUT}")


if __name__ == "__main__":
    main()
