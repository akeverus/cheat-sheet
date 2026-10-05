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
updated: "2026-10-05"
---
# Вопросы на собеседовании: Типизация в Python

Ориентир — Python 3.11–3.14. Синтаксис параметров типов `def f[T]` и `type Alias = ...` появился в Python 3.12; для поддержки 3.11 нужны прежние формы. Аннотации помогают проверить код статически, но не проверяют данные внешнего запроса при выполнении программы.

## Полезные ссылки

- [Python `typing`](https://docs.python.org/3/library/typing.html) — библиотека, поведение во время выполнения и версии синтаксиса.
- [Спецификация generics](https://typing.python.org/en/latest/spec/generics.html) — параметры типа и variance.
- [Спецификация Protocol](https://typing.python.org/en/latest/spec/protocol.html) — структурная совместимость.
- [Спецификация TypedDict](https://typing.python.org/en/latest/spec/typeddict.html) — ключи словарного контракта.

## Содержание

- [Q1. Что гарантируют аннотации типов при обработке внешних данных?](#q1-что-гарантируют-аннотации-типов-при-обработке-внешних-данных)
- [Q2. Как generic-функция сохраняет связь типов и что меняет Python 3.12?](#q2-как-generic-функция-сохраняет-связь-типов-и-что-меняет-python-312)
- [Q3. Когда Protocol удобнее общего базового класса?](#q3-когда-protocol-удобнее-общего-базового-класса)
- [Q4. Когда TypedDict полезен для словарного DTO и где его предел?](#q4-когда-typeddict-полезен-для-словарного-dto-и-где-его-предел)
- [See also](#see-also)

## Q1. Что гарантируют аннотации типов при обработке внешних данных?

Аннотации описывают ожидаемый контракт для анализатора типов и читателя кода. Сам интерпретатор **не заставляет** аргументы и возвращаемые значения ему соответствовать. Поэтому `def create_order(payload: dict[str, int])` не проверит JSON-запрос: декодер может передать словарь с другими ключами и значениями. На внешней границе нужны явная проверка, парсер или валидатор, а после неё статическая проверка помогает не потерять инварианты в остальном коде.

`Any` разрешает анализатору пропускать операции и ослабляет проверку; `object` принимает любое значение, но прежде чем вызвать специфичный метод, его тип нужно уточнить. `T | None` означает, что значение может быть `None`; это не то же самое, что необязательный аргумент с default. Параметр может иметь default и при этом иметь тип `int`.

```python
def double(value: int) -> int:
    return value * 2

double("a")  # Python выполнит это; статический анализатор сообщит об ошибке.
```

В Python 3.11–3.14 поведение вычисления и хранения аннотаций различается, поэтому код, читающий их во время выполнения, должен использовать документированный API для своей версии, а не считать сами аннотации валидатором. [Python `typing`](https://docs.python.org/3/library/typing.html).

## Q2. Как generic-функция сохраняет связь типов и что меняет Python 3.12?

Параметр типа выражает **связь** между входом и выходом. В `first[T](items: Sequence[T]) -> T` результат имеет тип элемента переданной последовательности; запись `-> object` эту связь теряет. Начиная с Python 3.12 параметр можно объявить прямо в `def` или `class`. Для кода, который должен запускаться на Python 3.11, используйте `TypeVar` и обычный синтаксис.

```python
from collections.abc import Sequence

def first[T](items: Sequence[T]) -> T:  # Python 3.12+
    return items[0]
```

Пустая последовательность вызовет `IndexError`: аннотация не доказывает, что элемент существует. `Sequence` обозначает интерфейс чтения и ковариантен по элементу; изменяемый `list[T]` инвариантен, потому что иначе через ссылку на `list[object]` можно было бы добавить несовместимое значение в `list[str]`. Для собственных generic-классов с новым синтаксисом анализатор выводит variance по использованию параметра; старый `TypeVar` сохраняет явную настройку. [Python `typing`](https://docs.python.org/3/library/typing.html#generics), [спецификация generics](https://typing.python.org/en/latest/spec/generics.html#variance-inference).

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

---

## See also

- [Основы Python](python-interview.md) — объекты, коллекции и dataclass.
- [FastAPI](../../frameworks/python/fastapi-interview.md) — валидация данных на HTTP-границе.
- [Тестирование в Python](python-testing-interview.md) — проверка поведения помимо статического контракта.
