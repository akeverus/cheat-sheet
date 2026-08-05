#!/usr/bin/env python3
"""Снимает спрайты `static/img/pixel/` на именованную палитру.

Зачем нужен отдельный инструмент, а не «поправить в редакторе». Набор из
66 PNG рисовался покадрово и разъехался ровно тем способом, каким разъехались
секционные стикеры: рядом с продуктовым токеном заводился почти-совпадающий
оттенок. Замер показал 28 цветов, из которых 18 не имели имени в `tokens.css`,
а четыре отличались от токена на 6–11 единиц суммарно по каналам — глазом не
поймать, но это вторая палитра.

Инструмент делает ровно одно: **точную замену цвета на цвет** по таблице ниже.
Он не сглаживает, не масштабирует, не перерисовывает. Поэтому результат
проверяем: число изменённых пикселей обязано совпасть с числом пикселей
исходного цвета, а всё остальное в файле — побайтово той же геометрии.

Таблица делится на две части, и это разные решения:

1. СНЯТИЕ НА ТОКЕН — цвет, у которого в продукте уже есть имя. Тут нормализуем
   не к большинству внутри набора, а к токену: у стикеров большинство само
   оказалось дрейфом, и «выровнять по большинству» закрепило бы ошибку.
2. СХЛОПЫВАНИЕ ДУБЛЯ — два оттенка одной роли внутри самого набора, не
   встречающиеся вместе либо различающиеся на десятки единиц при паре десятков
   пикселей. Остаётся один.

Оставшиеся безымянными цвета — не дрейф, а самостоятельные декоративные роли
(золото монеты, розовый сердца, дерево рукояти). Их нельзя снять на продуктовую
рампу, не сломав читаемость спрайта, поэтому они ПОЛУЧАЮТ ИМЕНА в `tokens.css`
как `--pixel-*`. После прогона каждый цвет набора обязан иметь имя — это и
проверяет `PixelSpriteContractTest`.

Запуск:  python3 scripts/normalize-pixel-palette.py [--dry-run]
"""

from __future__ import annotations

import argparse
import collections
import pathlib
import struct
import sys
import zlib

PIXEL = pathlib.Path(__file__).resolve().parents[1] / (
    "modules/quiz-app/src/main/resources/static/img/pixel"
)

# было -> стало. Комментарий фиксирует ПРИЧИНУ, иначе таблица через полгода
# неотличима от произвольного набора замен.
REMAP: dict[str, tuple[str, str]] = {
    # --- снятие на продуктовый токен ---
    "ffffff": ("eef6f5", "--color-text-primary: два белых в наборе (26 файлов чистый белый против 3 файлов продуктового)"),
    "0a1a20": ("071116", "--color-bg-primary: тёмная плитка мимо токена в 17 файлах, сам токен жил в одном"),
    "283c46": ("233a44", "--color-border-primary: d=9, рамка чекбокса"),
    "14232a": ("102029", "--color-surface-soft: d=8, заливка чекбокса"),
    "2db4aa": ("2bb8af", "--color-accent-primary-active: d=11, тень акцента на маскоте"),
    # --- схлопывание дубля внутри набора ---
    "ffd250": ("ffe65a", "d=30 к телу золота, встречается только вместе с ним"),
    "ff8cb4": ("ffa0b4", "d=20, та же роль «блик розового», файлы не пересекаются"),
    "ffdce6": ("ffa0b4", "третий тон розового ради трёх пикселей на весь набор"),
}


def read_png(path: pathlib.Path) -> tuple[int, int, list[bytearray]]:
    raw = path.read_bytes()
    if raw[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError(f"{path.name}: не PNG")
    pos, idat, width, height = 8, b"", None, None
    while pos < len(raw):
        length = struct.unpack(">I", raw[pos : pos + 4])[0]
        kind = raw[pos + 4 : pos + 8]
        if kind == b"IHDR":
            width, height, depth, ctype = struct.unpack(">IIBB", raw[pos + 8 : pos + 18])
            if (depth, ctype) != (8, 6):
                raise ValueError(f"{path.name}: ожидался 8-битный RGBA, получен {depth}/{ctype}")
        elif kind == b"IDAT":
            idat += raw[pos + 8 : pos + 8 + length]
        pos += 12 + length

    data = zlib.decompress(idat)
    stride = width * 4
    rows: list[bytearray] = []
    prev = bytearray(stride)
    i = 0
    for _ in range(height):
        filter_type = data[i]
        i += 1
        line = bytearray(data[i : i + stride])
        i += stride
        for x in range(stride):
            a = line[x - 4] if x >= 4 else 0
            b = prev[x]
            c = prev[x - 4] if x >= 4 else 0
            if filter_type == 1:
                line[x] = (line[x] + a) & 255
            elif filter_type == 2:
                line[x] = (line[x] + b) & 255
            elif filter_type == 3:
                line[x] = (line[x] + (a + b) // 2) & 255
            elif filter_type == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pred = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[x] = (line[x] + pred) & 255
        rows.append(line)
        prev = line
    return width, height, rows


def write_png(path: pathlib.Path, width: int, height: int, rows: list[bytearray]) -> None:
    """Пишет ровно IHDR/IDAT/IEND без фильтрации.

    Отсутствие служебных чанков — не эстетика: заявление о происхождении графики
    в `THIRD_PARTY_ASSETS.md` держится именно на структуре чанков, и её проверяет
    тест. Экспорт из редактора добавил бы pHYs/sRGB и сделал бы заявление ложным.
    """
    raw = b"".join(b"\x00" + bytes(line) for line in rows)

    def chunk(kind: bytes, payload: bytes) -> bytes:
        return (
            struct.pack(">I", len(payload))
            + kind
            + payload
            + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)
        )

    path.write_bytes(
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
        + chunk(b"IDAT", zlib.compress(raw, 9))
        + chunk(b"IEND", b"")
    )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    table = {
        bytes.fromhex(src): bytes.fromhex(dst) for src, (dst, _) in REMAP.items()
    }
    touched: dict[str, int] = {}
    expected = collections.Counter()
    changed_files = 0

    for path in sorted(PIXEL.glob("*.png")):
        width, height, rows = read_png(path)
        hits = 0
        for line in rows:
            for x in range(0, width * 4, 4):
                if line[x + 3] == 0:
                    continue
                dst = table.get(bytes(line[x : x + 3]))
                if dst is None:
                    continue
                expected["%02x%02x%02x" % tuple(line[x : x + 3])] += 1
                line[x : x + 3] = dst
                hits += 1
        if hits:
            touched[path.name] = hits
            changed_files += 1
            if not args.dry_run:
                write_png(path, width, height, rows)

    for name, hits in sorted(touched.items()):
        print(f"{name:22s} {hits:>5} px")
    print()
    for src, count in expected.most_common():
        dst, why = REMAP[src]
        print(f"#{src} -> #{dst}  {count:>5} px   {why}")
    print()
    print(f"файлов затронуто: {changed_files}, пикселей: {sum(touched.values())}")
    if args.dry_run:
        print("(dry-run: ничего не записано)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
