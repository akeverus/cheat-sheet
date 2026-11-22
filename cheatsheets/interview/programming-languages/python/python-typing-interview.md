---
title: "Вопросы на собеседовании: Типизация в Python"
description: "Аннотации, generics, Protocol и TypedDict на границах Python backend-приложения"
tags:
  - interview
  - programming-languages
  - python-typing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python typing interview"
  - "Типизация Python на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-11-02"
---
# Вопросы на собеседовании: Типизация в Python

Ориентир — Python 3.11–3.14; Q4–Q5 отдельно разбирают возможности Python 3.15. Синтаксис параметров типов `def f[T]` и `type Alias = ...` появился в Python 3.12; для поддержки 3.11 нужны прежние формы. Аннотации помогают проверить код статически, но не проверяют данные внешнего запроса при выполнении программы.

## Полезные ссылки

- [Python `typing`](https://docs.python.org/3/library/typing.html) — библиотека, поведение во время выполнения и версии синтаксиса.
- [Спецификация generics](https://typing.python.org/en/latest/spec/generics.html) — параметры типа и variance.
- [Спецификация Protocol](https://typing.python.org/en/latest/spec/protocol.html) — структурная совместимость.
- [Спецификация TypedDict](https://typing.python.org/en/latest/spec/typeddict.html) — ключи словарного контракта.
- [PEP 747: TypeForm](https://peps.python.org/pep-0747/) — выражение типа как аргумент API.

## Содержание

- [Q1. Что гарантируют аннотации типов при обработке внешних данных?](#q1-что-гарантируют-аннотации-типов-при-обработке-внешних-данных)
- [Q2. Как generic-функция сохраняет связь типов и что меняет Python 3.12?](#q2-как-generic-функция-сохраняет-связь-типов-и-что-меняет-python-312)
- [Q3. Когда Protocol удобнее общего базового класса?](#q3-когда-protocol-удобнее-общего-базового-класса)
- [Q4. Когда TypedDict полезен для словарного DTO и где его предел?](#q4-когда-typeddict-полезен-для-словарного-dto-и-где-его-предел)
- [Q5. Зачем нужен TypeForm при передаче схемы в Python 3.15?](#q5-зачем-нужен-typeform-при-передаче-схемы-в-python-315)
- [See also](#see-also)

## Q1. Что гарантируют аннотации типов при обработке внешних данных?

Аннотации описывают ожидаемый контракт для анализатора типов и читателя кода. Сам интерпретатор **не заставляет** аргументы и возвращаемые значения ему соответствовать. Поэтому `def create_order(payload: dict[str, int])` не проверит JSON-запрос: декодер может передать словарь с другими ключами и значениями. На внешней границе нужны явная проверка, парсер или валидатор, а после неё статическая проверка помогает не потерять инварианты в остальном коде.

`Any` разрешает анализатору пропускать операции и ослабляет проверку; `object` принимает любое значение, но прежде чем вызвать специфичный метод, его тип нужно уточнить. `T | None` означает, что значение может быть `None`; это не то же самое, что необязательный аргумент с default. Параметр может иметь default и при этом иметь тип `int`.

```python
def double(value: int) -> int:
    return value * 2

double("a")  # Python выполнит это; статический анализатор сообщит об ошибке.
```

В Python 3.11–3.13 аннотации по умолчанию вычисляются при объявлении; `from __future__ import annotations` сохраняет их как строки. Начиная с Python 3.14 они по умолчанию вычисляются отложенно при обращении. Код, который читает аннотации во время выполнения, должен учитывать этот режим: `annotationlib.get_annotations()` (3.14+) позволяет выбрать формат, а запрос вычисленных значений может выполнить код аннотации. Это по-прежнему не превращает аннотации в проверку HTTP-входа. [Python `typing`](https://docs.python.org/3/library/typing.html), [`annotationlib`: annotation semantics](https://docs.python.org/3.15/library/annotationlib.html#annotation-semantics).

**Пример для Python 3.14+.** Приложение регистрирует обработчик, а его тип импортируется только в блоке `TYPE_CHECKING`. При стандартной отложенной семантике функция объявится, но попытка получить вычисленную аннотацию через `get_annotations(save_price)` завершится `NameError`: имени `Decimal` во время выполнения нет.

```python
from annotationlib import Format, ForwardRef, get_annotations
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from decimal import Decimal

def save_price(price: Decimal) -> None:
    pass

annotations = get_annotations(save_price, format=Format.FORWARDREF)
assert isinstance(annotations["price"], ForwardRef)
assert get_annotations(save_price, format=Format.STRING)["price"] == "Decimal"
```

`FORWARDREF` позволяет отложить разрешение отсутствующего имени, а `STRING` подходит для текстового представления аннотации. Если фреймворку нужен сам класс для работы с данными, обеспечьте доступность имени во время выполнения. Форматы чтения не изолируют выполнение кода аннотаций. [Форматы annotationlib](https://docs.python.org/3.15/library/annotationlib.html#annotationlib.Format), [ограничения безопасности](https://docs.python.org/3.15/library/annotationlib.html#security-implications-of-introspecting-annotations).

Если проверку типа вынесли в отдельную функцию, `TypeIs` (Python 3.13+) передаёт её результат анализатору типов. Например, `def is_text_id(value: int | str) -> TypeIs[str]: return isinstance(value, str)` позволяет обработчику заказа считать идентификатор строкой в ветке `if is_text_id(value)` и целым числом в `else`. Уточнённый тип должен быть совместим с типом параметра.

Если предикат дополнительно проверяет `value.isdecimal()`, результат `False` уже не исключает строки. Для такой проверки подходит `TypeGuard[str]` (Python 3.10+): анализатор уточняет тип только в положительной ветке. Во время выполнения проверку делает тело предиката. [Спецификация Python typing](https://typing.python.org/en/latest/spec/narrowing.html).

## Q2. Как generic-функция сохраняет связь типов и что меняет Python 3.12?

Параметр типа выражает **связь** между входом и выходом. В `first[T](items: Sequence[T]) -> T` результат имеет тип элемента переданной последовательности; запись `-> object` эту связь теряет. Начиная с Python 3.12 параметр можно объявить прямо в `def` или `class`. Для кода, который должен запускаться на Python 3.11, используйте `TypeVar` и обычный синтаксис.

```python
from collections.abc import Sequence

def first[T](items: Sequence[T]) -> T:  # Python 3.12+
    return items[0]
```

Для Python 3.11 ту же связь выражают через `TypeVar`:

```python
from collections.abc import Sequence
from typing import TypeVar

T = TypeVar("T")

def first(items: Sequence[T]) -> T:
    return items[0]
```

Пустая последовательность вызовет `IndexError`: аннотация не доказывает, что элемент существует. `Sequence` обозначает интерфейс чтения и ковариантен по элементу; изменяемый `list[T]` инвариантен, потому что иначе через ссылку на `list[object]` можно было бы добавить несовместимое значение в `list[str]`. Для собственных generic-классов с новым синтаксисом анализатор выводит variance по использованию параметра; старый `TypeVar` сохраняет явную настройку. [Python `typing`](https://docs.python.org/3/library/typing.html#generics), [спецификация generics](https://typing.python.org/en/latest/spec/generics.html#variance-inference).

**Верхняя граница и набор допустимых типов.** Если функция возвращает один из переданных объектов, верхняя граница позволяет сохранить конкретный подтип. Если функция обрабатывает пару строк или пару байтов в рамках одного вызова, ограничение набором типов позволяет потребовать одну общую альтернативу.

```python
class OrderCode(str):
    pass

def pick_code[T: str](code: T, *, fallback: T) -> T:
    return code or fallback

def join_parts[T: (str, bytes)](left: T, right: T) -> T:
    return left + right
```

Для `pick_code(OrderCode("A"), fallback=OrderCode("B"))` анализатор может вывести `OrderCode`. В `join_parts(OrderCode("A"), OrderCode("B"))` он выводит `str`: подтип приводится к одной из перечисленных альтернатив при выводе типа. Вызов `join_parts("A", b"B")` анализатор отклонит, потому что единой альтернативы нет. В Python 3.11 те же объявления параметров записывают как `TypeVar("T", bound=str)` и `TypeVar("T", str, bytes)` соответственно. [Документация TypeVar](https://docs.python.org/3.15/library/typing.html#typing.TypeVar).

## Q3. Когда Protocol удобнее общего базового класса?

`Protocol` описывает нужные методы и атрибуты **структурно**: класс может удовлетворять контракту статически, даже не наследуясь от протокола. Это удобно на границе сервиса и адаптера, когда уже существующий клиент умеет нужную операцию, а заставлять его наследоваться от вашего ABC нельзя или незачем.

```python
from typing import Protocol

class Sender(Protocol):
    def send(self, message: str) -> None: ...

def notify(sender: Sender, message: str) -> None:
    sender.send(message)
```

Статический анализатор сравнивает сигнатуру `send`; обычный `isinstance(obj, Sender)` не работает. `@runtime_checkable` разрешает такую проверку, но она проверяет лишь наличие атрибутов, **не их типы или сигнатуры**. Для runtime-проверки входа это не полноценная валидация. Если важны общее состояние и поведение базового класса, обычное наследование или ABC может быть уместнее. [Спецификация Protocol](https://typing.python.org/en/latest/spec/protocol.html), [Python `typing`](https://docs.python.org/3/library/typing.html#typing.runtime_checkable).

**Граница для динамических адаптеров.** С Python 3.12 проверка `isinstance` для runtime-протоколов ищет атрибуты через `inspect.getattr_static`. Прокси, который предоставляет метод только через `__getattr__`, может успешно вызвать этот метод, но не пройти проверку протокола. Если архитектура плагинов опирается на такую проверку, объявите метод явно на адаптере.

```python
from typing import Protocol, runtime_checkable

@runtime_checkable
class Closable(Protocol):
    def close(self) -> None: ...

class PluginProxy:
    def __getattr__(self, name: str):
        if name == "close":
            return lambda: None
        raise AttributeError(name)

proxy = PluginProxy()
proxy.close()
assert not isinstance(proxy, Closable)  # Python 3.12+
```

Начиная с Python 3.12 набор членов runtime-протокола фиксируется при создании класса. Добавление атрибутов в класс протокола после этого не меняет проверку. В Python 3.15 полагаться на унаследованный маркер также устарело. Помечайте проверяемый подпротокол собственным `@runtime_checkable`. [Документация Python 3.15](https://docs.python.org/3.15/library/typing.html#typing.runtime_checkable).

**Проверка класса при регистрации плагина.** Для runtime-протокола, состоящего из методов, доступен и `issubclass`. Если контракт `NamedPlugin` содержит метод `run()` и атрибут `name: str`, проверка `issubclass(Plugin, NamedPlugin)` вызовет `TypeError`. Поле `name` может появиться только у экземпляра после `__init__`. Проверка `isinstance(Plugin(), NamedPlugin)` в таком случае может пройти, если у экземпляра есть оба члена. Она проверяет присутствие поля, поэтому тип его значения и сигнатуру метода по-прежнему проверяет статический анализатор или явная валидация. [Спецификация runtime-протоколов](https://typing.python.org/en/latest/spec/protocol.html#runtime-checkable-decorator-and-narrowing-types-by-isinstance).

## Q4. Когда TypedDict полезен для словарного DTO и где его предел?

`TypedDict` описывает известные ключи и типы значений **обычного `dict`** для статической проверки. Он полезен, когда код действительно обменивается словарями, например после декодирования JSON или между слоями с dict-контрактом. Ключи обязательны по умолчанию; `NotRequired` (Python 3.11+) делает конкретный ключ необязательным.

```python
from typing import NotRequired, TypedDict

class OrderPayload(TypedDict):
    order_id: int
    note: NotRequired[str]

payload: OrderPayload = {"order_id": 42}
```

Во время выполнения `payload` остаётся словарём: `TypedDict` не создаёт экземпляр-модель и **не проверяет** JSON, ключи или значения. Внешние данные сначала валидируют; `payload["note"]` без проверки присутствия может дать `KeyError`. Если нужны методы, инварианты объекта или отдельная runtime-модель, рассмотрите обычный класс или `dataclass`; его аннотации тоже сами по себе не валидируют вход. [Python `typing`](https://docs.python.org/3/library/typing.html#typing.TypedDict), [спецификация TypedDict](https://typing.python.org/en/latest/spec/typeddict.html).

В стандартном `typing.TypedDict` **Python 3.15** появились два варианта контракта для дополнительных ключей. `closed=True` сообщает анализатору типов, что сверх объявленных ключей других быть не должно. `extra_items=str` допускает дополнительные строковые ключи со значениями типа `str`; тип уже объявленного `order_id: int` от этого не меняется. Параметры `closed` и `extra_items` нельзя задать вместе. Это полезно, когда один словарный DTO должен иметь строго заданную форму, а другой — известные поля плюс расширения партнёра. [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html), [Python 3.15 `TypedDict`](https://docs.python.org/3.15/library/typing.html#typing.TypedDict), [PEP 728](https://peps.python.org/pep-0728/).

```python
class FixedOrder(TypedDict, closed=True):  # Python 3.15+
    order_id: int

class PartnerOrder(TypedDict, extra_items=str):  # Python 3.15+
    order_id: int

partner: PartnerOrder = {"order_id": 42, "source": "partner"}
```

`closed=True` и `extra_items` меняют **статическую проверку**, а не поведение декодера JSON или обычного `dict`. На входе сервиса по-прежнему нужны проверка обязательных полей, типов и политика обработки лишних ключей. Код, который запускается на Python 3.11–3.14 со стандартным `typing`, не должен использовать новые параметры объявления класса. [Спецификация TypedDict](https://typing.python.org/en/latest/spec/typeddict.html), [Python 3.15 `typing`](https://docs.python.org/3.15/library/typing.html#typing.TypedDict).

Обычный `TypedDict` без этих параметров считается **открытым** для структурной совместимости: значение с дополнительными полями может подходить к такому типу. Это не разрешение добавлять неизвестные поля в литерал или конструктор объявленного `TypedDict` без проверки анализатором. `extra_items=T` нужен, когда такие поля входят в явный контракт. [Спецификация TypedDict: openness](https://typing.python.org/en/latest/spec/typeddict.html#openness), [PEP 728](https://peps.python.org/pep-0728/).

`ReadOnly` (Python 3.13+) задаёт статический контракт для записи по конкретному ключу. Например, обработчику можно запретить заменять или удалять поле `tags`, сохранив возможность добавлять элементы в его список.

```python
from typing import ReadOnly, TypedDict

class OrderEvent(TypedDict):
    tags: ReadOnly[list[str]]

event: OrderEvent = {"tags": ["received"]}
# event["tags"] = ["processed"]  # Ошибка статической проверки
event["tags"].append("processed")  # Допустимо
assert event["tags"] == ["received", "processed"]
```

Если обработчику достаточно читать элементы, объявите `ReadOnly[Sequence[str]]`. Через такой тип анализатор также запретит вызов `append`. Если нужна неизменяемая последовательность во время выполнения, создайте отдельный `tuple`. Другой владелец исходного словаря всё ещё может изменить его. [PEP 705](https://peps.python.org/pep-0705/).

## Q5. Зачем нужен TypeForm при передаче схемы в Python 3.15?

Иногда функция получает **само выражение типа** как схему: класс `int`, объединение `str | None`, `list[int]` или класс `TypedDict`. Аннотация `type[T]` подходит для объектов классов, но не описывает все такие выражения. Начиная с Python 3.15, `TypeForm[T]` позволяет статически связать переданное выражение типа с типом результата. Это полезно в API парсера или валидатора, который принимает схему от вызывающего кода. [PEP 747](https://peps.python.org/pep-0747/), [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#typing).

```python
from typing import TypeForm

def parse[T](schema: TypeForm[T], raw: object) -> T:
    ...  # Здесь показана только сигнатура, а не реализация проверки.
```

Вызов `parse(str | None, raw)` связывает тип результата с `str | None` для анализатора типов. `TypeForm` описывает **статический контракт API**, а не готовый движок проверки. Само выражение `TypeForm(x)` во время выполнения возвращает `x`; из аннотации нельзя заключить, что `raw` проверен. Для внешнего JSON реализация должна отдельно обработать вложенные контейнеры, `TypedDict`, объединения и ошибки, либо использовать подходящий валидатор. Для Python 3.11–3.14 `TypeForm` отсутствует в стандартном `typing`; поддержку бэкпорта и анализатора типов проверяют отдельно. [PEP 747](https://peps.python.org/pep-0747/), [Python 3.15 `typing.TypeForm`](https://docs.python.org/3.15/library/typing.html#typing.TypeForm).

**Пример на входе API.** Если вызывающий код передал схему `list[int]`, анализатор ожидает результат типа `list[int]`. Допустим, контракт требует принимать только целые JSON-числа без преобразования строк и логических значений. Тогда `[1, 2]` проходит, а `[1, "2"]` и `[true]` отклоняются. Для этой конкретной схемы проверка после декодирования выглядит так.

```python
def matches_int_list(raw: object) -> bool:
    return isinstance(raw, list) and all(type(item) is int for item in raw)

assert matches_int_list([1, 2])
assert not matches_int_list([1, "2"])
assert not matches_int_list([True])
```

Вызов `isinstance(raw, list[int])` здесь вызовет `TypeError`. Если API принимает произвольные схемы, нужно явно определить поддерживаемые формы и поведение при неподдерживаемой схеме. [PEP 747: поддержка произвольных форм типов](https://peps.python.org/pep-0747/#challenges-when-accepting-all-typeforms), [PEP 585: обобщённые типы в isinstance](https://peps.python.org/pep-0585/#making-isinstance-obj-list-str-perform-a-runtime-type-check).

---

## See also

- [Основы Python](python-interview.md) — объекты, коллекции и dataclass.
- [FastAPI](../../frameworks/python/fastapi-interview.md) — валидация данных на HTTP-границе.
- [Тестирование в Python](python-testing-interview.md) — проверка поведения помимо статического контракта.
