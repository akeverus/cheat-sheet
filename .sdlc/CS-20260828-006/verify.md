# CS-20260828-006 — верификация (AC-3, негативный контроль валидатора)

Базовая линия перед негативными тестами: `python3 scripts/check-arch-anchors.py` →
`anchors=70 ok, symbols=171 ok, mappings=36 ok`, exit 0.

## A1 — несуществующий файл в якоре

Дописан `` `app:no/such/File.java:1` `` в конец документа.

```
A: якорь `app:no/such/File.java:1` — файл не существует: .../modules/quiz-app/src/main/java/com/cheatsheet/quiz/no/such/File.java
anchors=70 ok, symbols=171 ok, mappings=36 ok
exit=1
```

Откат: `cp` исходной копии обратно, `diff -q` — идентично, повторный прогон → exit 0.

## A2 — номер строки вне диапазона файла

Якорь `` `app:feature/interview/service/review/ReviewService.java:32` `` временно
заменён на `:999999`.

```
A: якорь `app:feature/interview/service/review/ReviewService.java:999999` — строка 999999 вне диапазона 1..210
anchors=69 ok, symbols=171 ok, mappings=36 ok
exit=1
```

Откат подтверждён (`diff -q` чисто), повторный прогон → exit 0.

## B — неизвестный токен

Дописано `` `NoSuchClassName` `` в конец документа.

```
B: неизвестный токен `NoSuchClassName` — не резолвится в *.java и не в allowlist
anchors=70 ok, symbols=171 ok, mappings=36 ok
exit=1
```

Откат подтверждён, повторный прогон → exit 0.

## C — удалена строка с `/api/streak`

Из §7 убрано вхождение `` `/api/streak` ``.

```
C: путь `/api/streak` из @*Mapping отсутствует в документе дословно
anchors=70 ok, symbols=171 ok, mappings=35 ok
exit=1
```

Откат подтверждён, повторный прогон → exit 0.

## Итог

Все четыре проверки валидатора (`A` — несуществующий файл, `A` — строка вне диапазона,
`B` — неизвестный токен, `C` — пропавший HTTP-путь) реально падают на порче и реально
зеленеют после отката. Рабочее дерево после всех четырёх экспериментов чистое, кроме
целевых `docs/backend-architecture.md` и `scripts/check-arch-anchors.py`.
