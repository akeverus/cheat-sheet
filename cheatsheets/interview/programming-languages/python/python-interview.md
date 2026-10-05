---
title: "Вопросы на собеседовании: Python — основы"
description: "Модель объектов Python, коллекции, функции, области видимости, итерация, исключения и data model для backend-разработчика"
tags:
  - interview
  - programming-languages
  - python-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python core interview"
  - "Основы Python на собеседовании"
prerequisites: []
next:
  - "[[python-concurrency-interview]]"
updated: "2026-10-05"
---
# Вопросы на собеседовании: Python — основы

Вопросы рассчитаны на Python 3.11–3.14; там, где важна реализация, речь идёт о CPython. Конкурентность вынесена в соседнюю тему.

## Полезные ссылки

- [Python data model](https://docs.python.org/3/reference/datamodel.html) — объекты, типы, специальные методы.
- [Python tutorial: data structures](https://docs.python.org/3/tutorial/datastructures.html) — коллекции и итерация.
- [Python tutorial: functions](https://docs.python.org/3/tutorial/controlflow.html#defining-functions) — аргументы, defaults и области видимости.
- [copy](https://docs.python.org/3/library/copy.html) и [dataclasses](https://docs.python.org/3/library/dataclasses.html) — копирование и классы данных.

## Содержание

- [Q1. Чем отличаются имя, объект, равенство и идентичность?](#q1-чем-отличаются-имя-объект-равенство-и-идентичность)
- [Q2. Что меняют присваивание, shallow copy и deep copy?](#q2-что-меняют-присваивание-shallow-copy-и-deep-copy)
- [Q3. Как выбрать list, tuple, set и dict?](#q3-как-выбрать-list-tuple-set-и-dict)
- [Q4. Почему важны hash и равенство ключа dict?](#q4-почему-важны-hash-и-равенство-ключа-dict)
- [Q5. Когда вычисляется значение аргумента по умолчанию?](#q5-когда-вычисляется-значение-аргумента-по-умолчанию)
- [Q6. Как работают области видимости и замыкания?](#q6-как-работают-области-видимости-и-замыкания)
- [Q7. Чем iterable отличается от iterator и generator?](#q7-чем-iterable-отличается-от-iterator-и-generator)
- [Q8. Как обеспечивать освобождение ресурса при исключении?](#q8-как-обеспечивать-освобождение-ресурса-при-исключении)
- [Q9. Что дают dataclass и специальные методы класса?](#q9-что-дают-dataclass-и-специальные-методы-класса)
- [See also](#see-also)

## Q1. Чем отличаются имя, объект, равенство и идентичность?

Переменная в Python — **имя, связанное с объектом**, а не коробка со значением. У объекта есть идентичность, тип и значение. `==` вызывает сравнение значений (обычно `__eq__`), `is` сравнивает идентичность. Для проверки отсутствия значения пишут `x is None`; для содержимого — `x == expected`.

```python
a = [1]
b = a
c = [1]
assert a is b and a == c and a is not c
```

Изменяемый объект (`list`, `dict`, `set`) можно изменить через любой alias. У неизменяемого (`int`, `str`, `tuple`) операция изменения создаёт/выбирает другой объект. `tuple` может содержать изменяемый объект: неизменность контейнера не замораживает вложенный `list`. Не полагайтесь на `is` для чисел/строк: переиспользование таких объектов — деталь реализации. [Data model](https://docs.python.org/3/reference/datamodel.html#objects-values-and-types).

## Q2. Что меняют присваивание, shallow copy и deep copy?

`b = a` создаёт ещё одно имя для **того же** объекта. `copy.copy(a)` создаёт внешний контейнер, но вложенные элементы остаются общими. `copy.deepcopy(a)` рекурсивно копирует вложенные объекты; он ведёт таблицу уже скопированных объектов, чтобы сохранять общие ссылки и обрабатывать циклы. При внешних ресурсах и специальных классах «глубокая копия» не всегда имеет полезный смысл.

```python
from copy import copy, deepcopy
source = [[1]]
shallow, deep = copy(source), deepcopy(source)
source[0].append(2)
assert shallow == [[1, 2]] and deep == [[1]]
```

На границе API явно решайте, можно ли передавать изменяемый объект по общей ссылке. Копируйте лишь нужный слой данных и учитывайте стоимость полной копии. [copy](https://docs.python.org/3/library/copy.html).

## Q3. Как выбрать list, tuple, set и dict?

`list` — упорядоченная изменяемая последовательность с индексом; `tuple` — неизменяемая последовательность; `set` — множество уникальных hashable элементов; `dict` — соответствие hashable ключей значениям с порядком вставки в современных Python 3. У `dict` и `set` операции поиска в среднем быстры, но нельзя обещать константное время для любого входа.

Для API ответа `dict` удобен как отображение имён полей, а фиксированная пара значений может быть `tuple` или именованной структурой. Если порядок важен, не берите `set`; если нужен hashable ключ, изменяемый `list` не подходит. [Built-in types](https://docs.python.org/3/library/stdtypes.html).

## Q4. Почему важны hash и равенство ключа dict?

`dict` сначала использует `hash(key)`, затем равенство для различения совпавших хешей. Если два объекта равны, их хеши **обязаны** совпадать. Ключ должен оставаться hashable: изменение полей, участвующих в hash/eq, после вставки нарушает поиск. Встроенные `list` и `dict` поэтому не годятся как ключи.

```python
lookup = {(1, 2): "ok"}
assert lookup[(1, 2)] == "ok"
# lookup[[1, 2]] = "bad"  # TypeError: unhashable type
```

Переопределяя `__eq__` у собственного класса, согласуйте `__hash__`; для неизменяемой value-модели помогает `@dataclass(frozen=True)`, но вложенные изменяемые поля всё равно требуют проверки. [Hashable](https://docs.python.org/3/glossary.html#term-hashable).

## Q5. Когда вычисляется значение аргумента по умолчанию?

Выражение default вычисляется **один раз при выполнении `def`**, а не при каждом вызове. Поэтому изменяемый default может случайно накопить состояние между запросами:

```python
def add(value, items=None):
    if items is None:
        items = []
    items.append(value)
    return items
```

`None` здесь — sentinel для нового списка; если `None` само по себе допустимое значение, нужен отдельный уникальный sentinel. Позиционные/keyword-only параметры (`/`, `*`) позволяют явно задать контракт вызова. [Python tutorial](https://docs.python.org/3/tutorial/controlflow.html#default-argument-values).

## Q6. Как работают области видимости и замыкания?

Поиск имени идёт от локальной области к окружающим функциям, модулю и builtins. Присваивание внутри функции делает имя локальным, если не объявлены `nonlocal` или `global`. Замыкание хранит **ссылку на переменную**, поэтому отложенные функции в цикле часто видят её последнее значение.

```python
callbacks = [lambda n=n: n for n in range(3)]
assert [f() for f in callbacks] == [0, 1, 2]
```

Default `n=n` фиксирует текущее значение при создании функции. `nonlocal` применяйте для изменения переменной ближайшей окружающей функции, но для состояния сервиса часто яснее явный объект. [Execution model](https://docs.python.org/3/reference/executionmodel.html#resolution-of-names).

## Q7. Чем iterable отличается от iterator и generator?

`iterable` обычно предоставляет `__iter__` (также возможен старый протокол последовательности); `iterator` возвращает себя из `__iter__` и выдаёт элементы через `__next__` до `StopIteration`. Генераторная функция с `yield` создаёт iterator с отложенным выполнением: тело начинает работать при первом продвижении, а не при вызове функции.

```python
def ids(rows):
    for row in rows:
        yield row["id"]

stream = ids([{"id": 1}, {"id": 2}])
assert next(stream) == 1
assert list(stream) == [2]
```

Один iterator обычно исчерпывается; не предполагайте повторный проход как по списку. Для БД закрытие cursor/Rows должно быть управляемым: ленивость сама по себе ресурс не освобождает. [Iterator types](https://docs.python.org/3/library/stdtypes.html#iterator-types).

## Q8. Как обеспечивать освобождение ресурса при исключении?

`try/finally` выполняет cleanup при обычном выходе и при исключении. Контекстный менеджер (`with`) выражает тот же ресурсный контракт через `__enter__`/`__exit__`; `__exit__` может подавить исключение, если вернёт истинное значение. Обычно ошибку пропускают дальше или преобразуют с `raise ... from exc`, сохраняя причину.

```python
with open("input.txt", encoding="utf-8") as source:
    data = source.read()
```

Не рассчитывайте на сборщик мусора для закрытия файла или соединения. Для асинхронных ресурсов используется `async with` и асинхронный протокол. [Data model: context managers](https://docs.python.org/3/reference/datamodel.html#context-managers).

## Q9. Что дают dataclass и специальные методы класса?

`@dataclass` генерирует `__init__`, `__repr__`, `__eq__` и другие методы в зависимости от параметров. Это удобно для небольших value-моделей, но не делает вложенные поля неизменяемыми и не заменяет валидацию входа. Для изменяемых полей используйте `field(default_factory=list)`, чтобы экземпляры не делили один список.

```python
from dataclasses import dataclass, field

@dataclass
class Batch:
    ids: list[int] = field(default_factory=list)
```

`frozen=True` запрещает обычное присваивание полям, `slots=True` (Python 3.10+) генерирует слоты; обе опции имеют границы и не являются автоматической оптимизацией любого класса. Из специальных методов важны `__iter__`, `__enter__`, `__eq__` и `__hash__`: реализация протокола меняет работу стандартных операций. [dataclasses](https://docs.python.org/3/library/dataclasses.html).

---

## See also

- [Конкурентность в Python](python-concurrency-interview.md) — задачи, отмена, потоки и процессы.
- [Типизация в Python](python-typing-interview.md) — статический контракт и граница runtime-валидации.
- [Упаковка Python-приложений](python-packaging-interview.md) — сборка и повторяемая установка.
- [SQL](../../databases/sql-interview.md) — общие принципы запросов и транзакций.
