---
title: "Вопросы на собеседовании: Python — основы"
description: "Модель объектов Python, коллекции, функции, итерация, ресурсы и кодировки текстового I/O для backend-разработчика"
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
updated: "2026-11-05"
---
# Вопросы на собеседовании: Python — основы

Основной материал рассчитан на Python 3.11–3.14; изменения Python 3.15 отдельно отмечены в Q3, Q5, Q10–Q12. Там, где важна реализация, речь идёт о CPython. Конкурентность вынесена в соседнюю тему.

## Полезные ссылки

- [Python data model](https://docs.python.org/3/reference/datamodel.html) — объекты, типы, специальные методы.
- [Python tutorial: data structures](https://docs.python.org/3/tutorial/datastructures.html) — коллекции и итерация.
- [Python tutorial: functions](https://docs.python.org/3/tutorial/controlflow.html#defining-functions) — аргументы, defaults и области видимости.
- [copy](https://docs.python.org/3/library/copy.html) и [dataclasses](https://docs.python.org/3/library/dataclasses.html) — копирование и классы данных.

## Содержание

- [Q1. Чем отличаются имя, объект, равенство и идентичность?](#q1-чем-отличаются-имя-объект-равенство-и-идентичность)
- [Q2. Что меняют присваивание, shallow copy и deep copy?](#q2-что-меняют-присваивание-shallow-copy-и-deep-copy)
- [Q3. Как выбрать list, tuple, set, dict и frozendict?](#q3-как-выбрать-list-tuple-set-dict-и-frozendict)
- [Q4. Почему важны hash и равенство ключа dict?](#q4-почему-важны-hash-и-равенство-ключа-dict)
- [Q5. Когда вычисляется значение аргумента по умолчанию и зачем нужен sentinel?](#q5-когда-вычисляется-значение-аргумента-по-умолчанию-и-зачем-нужен-sentinel)
- [Q6. Как работают области видимости и замыкания?](#q6-как-работают-области-видимости-и-замыкания)
- [Q7. Чем iterable отличается от iterator и generator?](#q7-чем-iterable-отличается-от-iterator-и-generator)
- [Q8. Как обеспечивать освобождение ресурса при исключении?](#q8-как-обеспечивать-освобождение-ресурса-при-исключении)
- [Q9. Что дают dataclass и специальные методы класса?](#q9-что-дают-dataclass-и-специальные-методы-класса)
- [Q10. Как смена кодировки по умолчанию в Python 3.15 влияет на чтение текстовых файлов?](#q10-как-смена-кодировки-по-умолчанию-в-python-315-влияет-на-чтение-текстовых-файлов)
- [Q11. Как lazy import в Python 3.15 влияет на старт сервиса и первый запрос?](#q11-как-lazy-import-в-python-315-влияет-на-старт-сервиса-и-первый-запрос)
- [Q12. Как профилировать работающий Python-сервис в 3.15?](#q12-как-профилировать-работающий-python-сервис-в-315)
- [See also](#see-also)

## Q1. Чем отличаются имя, объект, равенство и идентичность?

Переменная в Python — **имя, связанное с объектом**, а не коробка со значением. У объекта есть идентичность, тип и значение. `is` проверяет, указывают ли операнды на один объект. `==` использует протокол сравнения `__eq__`. Если ни один подходящий метод не вернул результат, отличный от `NotImplemented`, Python проверяет идентичность. Для проверки отсутствия значения пишут `x is None`; для содержимого — `x == expected`.

```python
a = [1]
b = a
c = [1]
assert a is b and a == c and a is not c
```

Изменяемый объект (`list`, `dict`, `set`) можно изменить через любой alias. Операции над неизменяемым объектом (`int`, `str`, `tuple`) сохраняют его состояние. Выражение может вернуть другой объект, а присваивание свяжет имя с результатом. `tuple` может содержать изменяемый объект: неизменность контейнера не замораживает вложенный `list`. Не полагайтесь на `is` для чисел/строк: переиспользование таких объектов — деталь реализации. [Data model](https://docs.python.org/3/reference/datamodel.html#objects-values-and-types).

Идентичность сама по себе не гарантирует истинность `==`. Например, значение NaN не равно самому себе. Если проверяете пропуск измерения, представленный NaN, используйте `math.isnan()`. [Протокол сравнения](https://docs.python.org/3/reference/datamodel.html#object.__eq__).

```python
from math import isnan

measurement = float("nan")
same = measurement
assert same is measurement
assert not (same == measurement)
assert isnan(measurement)
```

## Q2. Что меняют присваивание, shallow copy и deep copy?

`b = a` привязывает имя `b` к тому же объекту. Для обычного списка `copy.copy(a)` создаёт новый список со ссылками на прежние элементы. Поэтому замена элемента в копии меняет только копию, а изменение общего вложенного объекта видно через оба списка. `copy.deepcopy(a)` обходит вложенные объекты и использует словарь `memo`, чтобы сохранять общие ссылки между копиями и обрабатывать циклы.

```python
from copy import copy, deepcopy
source = [[1]]
shallow, deep = copy(source), deepcopy(source)
source[0].append(2)
assert shallow == [[1, 2]] and deep == [[1]]
```

Поведение зависит от типа объекта. Для функций и классов обе операции возвращают исходный объект. Файл или сокет таким способом скопировать нельзя. Пользовательский класс может определить `__copy__` и `__deepcopy__`, например чтобы сохранить общую ссылку на объект конфигурации. Если `__deepcopy__` копирует отдельный компонент, ему нужно передать в рекурсивный вызов тот же `memo`.

На границе API явно решайте, можно ли передавать изменяемый объект по общей ссылке. Копируйте лишь нужный слой данных и учитывайте стоимость полной копии. [copy](https://docs.python.org/3/library/copy.html).

## Q3. Как выбрать list, tuple, set, dict и frozendict?

`list` — упорядоченная изменяемая последовательность с индексом; `tuple` — неизменяемая последовательность; `set` — множество уникальных hashable элементов; `dict` — соответствие hashable ключей значениям с порядком вставки в современных Python 3. У `dict` и `set` операции поиска в среднем быстры, но нельзя обещать константное время для любого входа.

Для API ответа `dict` удобен как отображение имён полей, а фиксированная пара значений может быть `tuple` или именованной структурой. Если порядок важен, не берите `set`; если нужен hashable ключ, изменяемый `list` не подходит. [Built-in types](https://docs.python.org/3/library/stdtypes.html).

В Python 3.15 появился встроенный `frozendict` для отображения, чьи пары ключ–значение нельзя менять после создания. Он сохраняет порядок вставки, но не является подклассом `dict`. Если функция только читает отображение, контракт `collections.abc.Mapping` позволит принять и `dict`, и `frozendict`; код с `isinstance(value, dict)` второй тип не примет. Для Python 3.11–3.14 встроенного `frozendict` нет. [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-814-add-frozendict-built-in-type), [PEP 814](https://peps.python.org/pep-0814/).

```python
key = frozendict(region="ru", tier="basic")  # Python 3.15+
cache = {key: "ready"}

source = {"limits": [10]}
frozen = frozendict(source)
source["limits"].append(20)
assert frozen["limits"] == [10, 20]
```

`frozendict` хешируем, только если хешируемы **и ключи, и значения**. Создание из `dict` копирует внешний уровень: изменить пару в `frozen` нельзя, но вложенный список остался общим и изменяемым; `hash(frozen)` здесь вызовет `TypeError`. Поэтому для ключа кэша используйте хешируемые значения и не считайте `frozendict` рекурсивной заморозкой. Общий контракт хеша для ключей разобран в [Q4](#q4-почему-важны-hash-и-равенство-ключа-dict). [Python 3.15 `frozendict`](https://docs.python.org/3.15/builtins/stdtypes.html#mapping-types-dict-frozendict), [PEP 814](https://peps.python.org/pep-0814/).

Если потребитель должен видеть обновления конфигурации, но ему нужен интерфейс только для чтения, подойдёт `MappingProxyType`. Изменения исходного словаря сразу видны через представление. Для фиксации набора пар на момент передачи подходит `frozendict`: последующие замены значений в исходном словаре не меняют его пары. В обоих случаях вложенные изменяемые значения требуют отдельного решения. [MappingProxyType](https://docs.python.org/3/library/types.html#types.MappingProxyType).

```python
from types import MappingProxyType

config = {"timeout": 3}
live = MappingProxyType(config)
snapshot = frozendict(config)  # Python 3.15+
config["timeout"] = 5

assert live["timeout"] == 5
assert snapshot["timeout"] == 3
```

## Q4. Почему важны hash и равенство ключа dict?

`dict` использует `hash(key)` и равенство для поиска ключа и различения коллизий. Если два объекта равны, их хеши **обязаны** совпадать. Хеш хешируемого объекта должен оставаться стабильным при жизни объекта. Пока объект служит ключом, изменение полей, участвующих в сравнении по значению, также может нарушить поиск. У встроенных `list` и `dict` хеша нет. У кортежа хеш есть, если все его элементы хешируемы.

```python
lookup = {(1, 2): "ok"}
assert lookup[(1, 2)] == "ok"
# lookup[[1, 2]] = "bad"  # TypeError: unhashable type
```

Изменяемость сама по себе не мешает использовать экземпляр как ключ. Например, класс с унаследованными от `object` равенством и хешем сохраняет ключ при изменении атрибутов. Словарь с таким ключом адресует конкретный экземпляр.

```python
class Job:
    pass

job = Job()
job.status = "queued"
results = {job: "report.csv"}
job.status = "done"
assert results[job] == "report.csv"
assert Job() not in results
```

Если класс переопределяет `__eq__`, но не определяет `__hash__`, Python устанавливает `__hash__ = None` и экземпляры нельзя использовать как ключи. Для неизменяемой value-модели помогает `@dataclass(frozen=True)`, но вложенные изменяемые поля всё равно требуют проверки. [Data model: `__hash__`](https://docs.python.org/3/reference/datamodel.html#object.__hash__), [Hashable](https://docs.python.org/3/glossary.html#term-hashable).

## Q5. Когда вычисляется значение аргумента по умолчанию и зачем нужен sentinel?

Выражение default вычисляется **один раз при выполнении `def`**, а не при каждом вызове. Поэтому изменяемый default может случайно накопить состояние между запросами:

```python
def add_with_shared_default(value, items=[]):
    items.append(value)
    return items

assert add_with_shared_default("first") == ["first"]
assert add_with_shared_default("second") == ["first", "second"]
```

Если каждому вызову без списка нужен собственный список, создавайте его внутри функции:

```python
def add(value, items=None):
    if items is None:
        items = []
    items.append(value)
    return items

assert add("first") == ["first"]
assert add("second") == ["second"]
```

`None` здесь обозначает отсутствие аргумента. Если `None` — допустимое значение входа, такой default уже не отличит пропуск от явного `None`. В Python 3.15 для этого есть встроенный `sentinel()`:

```python
MISSING = sentinel("MISSING")  # Python 3.15+, один объект на уровне модуля

def change_note(value: str | None | MISSING = MISSING) -> str:
    if value is MISSING:
        return "оставить заметку"
    if value is None:
        return "удалить заметку"
    return "заменить заметку"
```

`change_note()` и `change_note(None)` теперь различаются. Сравнивайте с тем же объектом через `is`: каждый новый вызов `sentinel("MISSING")` создаёт другой sentinel, несмотря на одинаковое имя. Sentinel истинен в логическом контексте, поэтому `if not value` не заменяет проверку идентичности. В Python 3.11–3.14 встроенного `sentinel()` нет; для простого случая подойдёт объект `MISSING = object()` на уровне модуля. Если `None` не является допустимым значением, исходный `items=None` остаётся проще. Позиционные/keyword-only параметры (`/`, `*`) позволяют отдельно задать контракт вызова. [Python tutorial](https://docs.python.org/3/tutorial/controlflow.html#default-argument-values), [Python 3.15 `sentinel()`](https://docs.python.org/3.15/builtins/functions.html#sentinel), [PEP 661](https://peps.python.org/pep-0661/).

**Граница копирования.** Если на входе обновления профиля поле отсутствовало, а код положил `MISSING` в словарь и затем сделал `deepcopy`, проверка `copied["note"] is MISSING` с обычным `object()` перестанет распознавать пропуск. У встроенного `sentinel()` в Python 3.15 и поверхностная, и глубокая копия возвращают исходный объект. Для `pickle` идентичность восстанавливается, когда sentinel доступен в модуле под своим именем, как `MISSING = sentinel("MISSING")`. В JSON-протоколе сохраняйте саму семантику обновления. Отсутствующее поле означает «оставить как есть», а явное `null` — «очистить». [PEP 661](https://peps.python.org/pep-0661/#specification).

## Q6. Как работают области видимости и замыкания?

Поиск имени идёт от локальной области к окружающим функциям, модулю и builtins. Присваивание внутри функции делает имя локальным, если не объявлены `nonlocal` или `global`. Замыкание хранит **ссылку на переменную**, поэтому отложенные функции в цикле часто видят её последнее значение.

```python
late = [lambda: n for n in range(3)]
assert [f() for f in late] == [2, 2, 2]

callbacks = [lambda n=n: n for n in range(3)]
assert [f() for f in callbacks] == [0, 1, 2]
```

Default `n=n` фиксирует текущее значение при создании функции. `nonlocal` позволяет перепривязать имя в ближайшей окружающей функции, в которой оно уже связано. Для состояния сервиса часто яснее явный объект.

**Чтение до локального присваивания.** Область имени определяется по всему телу функции. Даже условное присваивание делает имя локальным. Если условие ложно и локальное имя осталось без значения, чтение возбуждает `UnboundLocalError`, даже если в модуле есть одноимённая настройка.

```python
timeout = 30

def request_timeout(is_batch):
    if is_batch:
        timeout = 120
    return timeout

assert request_timeout(True) == 120
request_timeout(False)  # UnboundLocalError
```

Для выбора настройки достаточно `return 120 if is_batch else timeout`. Тогда функция читает модульное значение в обычной ветке и не меняет его. [Execution model](https://docs.python.org/3/reference/executionmodel.html#resolution-of-names).

## Q7. Чем iterable отличается от iterator и generator?

`iterable` обычно предоставляет `__iter__` (также возможен старый протокол последовательности). Вызов `iter(value)` получает из него iterator. Iterator возвращает себя из `__iter__`, а `next(iterator)` вызывает его `__next__`: тот возвращает следующий элемент или возбуждает `StopIteration`. Генераторная функция с `yield` создаёт iterator с отложенным выполнением: тело начинает работать при первом продвижении, а не при вызове функции.

```python
def ids(rows):
    for row in rows:
        yield row["id"]

stream = ids([{"id": 1}, {"id": 2}])
assert next(stream) == 1
assert list(stream) == [2]
```

После исчерпания итератор продолжает возбуждать `StopIteration`. Для повторного прохода нужен новый итератор, если источник его поддерживает. [Iterator types](https://docs.python.org/3/library/stdtypes.html#iterator-types).

**Досрочный выход.** Если генератор управляет файлом через `with` внутри своего тела, он остаётся открытым, пока генератор приостановлен на `yield`. Сам `break` выходит из цикла без вызова `close()`. Если вызывающий код владеет этим генератором, `contextlib.closing` закроет его при выходе из блока, даже при исключении.

```python
from contextlib import closing
from io import StringIO

def lines(source):
    with source:
        yield from source

source = StringIO("first\nsecond\n")
with closing(lines(source)) as stream:
    for line in stream:
        assert line == "first\n"
        break
    assert not source.closed

assert source.closed
```

Здесь `close()` завершает приостановленный генератор через `GeneratorExit` и управление выходит из внутреннего `with`. Если вызывающий код сам открыл файл или курсор и передал его как заимствованный ресурс, обычно именно вызывающий код задаёт внешний `with`. Метод `close()` входит в API генераторов, а произвольный итератор может его не иметь. [Python Language Reference — generator.close](https://docs.python.org/3.14/reference/expressions.html#generator.close), [Python contextlib.closing](https://docs.python.org/3.14/library/contextlib.html#contextlib.closing).

## Q8. Как обеспечивать освобождение ресурса при исключении?

`try/finally` выполняет cleanup при обычном выходе и при исключении. Одна ветка `except` не покрывает успешный выход и другие типы ошибок. Контекстный менеджер (`with`) выражает ресурсный контракт через `__enter__`/`__exit__`; `__exit__` может подавить исключение, если вернёт истинное значение. Обычно ошибку пропускают дальше или преобразуют с `raise ... from exc`, сохраняя причину. `return` или новое исключение внутри `finally` может скрыть первоначальную ошибку, поэтому не помещайте туда управление потоком без необходимости.

```python
with open("input.txt", encoding="utf-8") as source:
    data = source.read()
```

`with` вызывает `__exit__` после успешного `__enter__`, в том числе при исключении в теле блока. Если исключение возникло в самом `__enter__`, Python не вызовет `__exit__` этого менеджера. Если менеджер уже успел выделить ресурс, его код входа должен сам обеспечить очистку при сбое следующих шагов.

Например, при открытии входного файла и второго файла с настройками используйте `with open(input_path) as source, open(config_path) as config:`. Если второе открытие завершится ошибкой, тело блока пропускается, а первый файл закрывается. Несколько менеджеров в одном `with` работают как вложенные блоки. Для числа ресурсов, которое известно только во время выполнения, используйте `contextlib.ExitStack`. Он закрывает успешно добавленные менеджеры в обратном порядке при выходе из блока. [Справочник Python по with](https://docs.python.org/3/reference/compound_stmts.html#the-with-statement), [contextlib.ExitStack](https://docs.python.org/3/library/contextlib.html#contextlib.ExitStack).

Если набор ресурсов после проверки должен перейти к другому владельцу, используйте `ExitStack.pop_all()`. Например, сервис открывает несколько файлов для отложенной отправки. Если очередной файл открыть не удалось, исходный стек закроет уже открытые файлы. После успешного открытия `pop_all()` перенесёт зарегистрированные операции очистки в новый стек без их выполнения. Передайте новый стек вместе с файлами. Получатель должен войти в него через `with` или явно вызвать `close()`, иначе выход из исходного блока файлы уже не закроет. [Документация Python — ExitStack.pop_all](https://docs.python.org/3/library/contextlib.html#contextlib.ExitStack.pop_all).

Не рассчитывайте на сборщик мусора для закрытия файла или соединения. Для асинхронных ресурсов используется `async with` и асинхронный протокол. [Data model: context managers](https://docs.python.org/3/reference/datamodel.html#context-managers).

## Q9. Что дают dataclass и специальные методы класса?

`@dataclass` генерирует `__init__`, `__repr__`, `__eq__` и другие методы в зависимости от параметров. Это удобно для небольших value-моделей, но не делает вложенные поля неизменяемыми и не заменяет валидацию входа. Для изменяемого поля используйте `field(default_factory=list)`: фабрика вызывается для экземпляра, когда значение поля не передано, поэтому такие экземпляры не делят один список.

```python
from dataclasses import dataclass, field

@dataclass
class Batch:
    ids: list[int] = field(default_factory=list)
```

Если явно передать один и тот же список двум экземплярам, фабрика не вызывается и список остаётся общим.

`frozen=True` запрещает обычное присваивание полям, `slots=True` (Python 3.10+) генерирует слоты; обе опции имеют границы и не являются автоматической оптимизацией любого класса. Из специальных методов важны `__iter__`, `__enter__`, `__eq__` и `__hash__`: реализация протокола меняет работу стандартных операций. [dataclasses](https://docs.python.org/3/library/dataclasses.html).

**Ключ кеша.** Если свой `__hash__` не задан, стандартный изменяемый `@dataclass(eq=True)` нельзя использовать как ключ словаря. При `eq=True, frozen=True` декоратор генерирует хеш. Он вычисляется по полям, участвующим в хешировании, поэтому хешируемость самого класса ещё не гарантирует, что `hash(instance)` успешно отработает. Например, включённый в хеш список вызовет `TypeError`, хотя поле нельзя переприсвоить. Для ключа с набором идентификаторов в заданном порядке используйте кортеж хешируемых значений.

```python
from dataclasses import dataclass

@dataclass(frozen=True)
class BatchKey:
    tenant_id: str
    ids: tuple[int, ...]

cache = {BatchKey("shop", (1, 2)): "ready"}
assert cache[BatchKey("shop", (1, 2))] == "ready"
```

Два отдельных экземпляра с одинаковыми значениями находят одну запись. `unsafe_hash=True` тоже генерирует метод, но не делает поля неизменяемыми. Если после вставки поменяется значение, участвующее в сравнении и хешировании, поиск по ключу может перестать работать. [Правила генерации хеша в dataclasses](https://docs.python.org/3.14/library/dataclasses.html#dataclasses.dataclass).

## Q10. Как смена кодировки по умолчанию в Python 3.15 влияет на чтение текстовых файлов?

В Python 3.15 режим UTF-8 включён по умолчанию. При включённом режиме текстовый `open(path)` без `encoding` использует UTF-8 независимо от системной локали. Если режим отключён при запуске, выбор кодировки снова зависит от локали. На системе с локалью UTF-8 разницы при этом может не быть. В Python 3.11–3.14 кодировка без явного аргумента могла зависеть от локали, если режим UTF-8 не был включён. Кодировка исходного `.py` файла уже была UTF-8 раньше; изменение касается прежде всего текстового ввода-вывода. [What's New in Python 3.15](https://docs.python.org/3.15/whatsnew/3.15.html#other-language-changes), [PEP 686](https://peps.python.org/pep-0686/).

Для файлов с известным форматом задавайте кодировку явно: `encoding="utf-8"` для UTF-8, конкретную legacy-кодировку для старого обменного файла. Если контракт действительно требует кодировку текущей локали, используйте `encoding="locale"` (доступно с Python 3.10). Так поведение не меняется неожиданно при смене версии, ОС или настроек запуска. UTF-8 по умолчанию **не** распознаёт кодировку файла автоматически: старый файл может вызвать `UnicodeDecodeError`, а ошибка декодирования не становится допустимой только потому, что версия новая. [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#other-language-changes), [io: Text Encoding](https://docs.python.org/3.15/library/io.html#text-encoding).

При обновлении сервиса найдите чтение и запись текста без `encoding`, включив `-X warn_default_encoding` или `PYTHONWARNDEFAULTENCODING=1`, и проверьте реальные входные файлы в окружении запуска. `PYTHONUTF8=0` или `-X utf8=0` временно возвращает прежнее поведение для диагностики; устойчивое исправление — явный контракт кодировки на границе данных. Закрытие файлов через `with` разобрано в [Q8](#q8-как-обеспечивать-освобождение-ресурса-при-исключении). [What's New in Python 3.15](https://docs.python.org/3.15/whatsnew/3.15.html#other-language-changes), [io: EncodingWarning](https://docs.python.org/3.15/library/io.html#opt-in-encodingwarning).

## Q11. Как lazy import в Python 3.15 влияет на старт сервиса и первый запрос?

В Python 3.15 `lazy import module` откладывает загрузку модуля до **первого использования импортированного имени**. Если тяжёлая зависимость нужна лишь редкому маршруту, процесс может стартовать быстрее. Но работа не исчезает: первый запрос к этому маршруту заплатит за импорт. Измеряйте и время старта, и задержку первого обращения, а не только одну метрику. [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-810-explicit-lazy-imports), [PEP 810](https://peps.python.org/pep-0810/).

```python
lazy import report_renderer  # Python 3.15+, только на уровне модуля

def render_report(data):
    return report_renderer.render(data)  # здесь модуль загрузится впервые
```

Ошибка импорта и побочные эффекты верхнего уровня модуля тоже переходят к первому использованию. Это опасно для регистрации обработчиков или плагинов, которая должна завершиться до приёма запросов: такой модуль импортируйте обычным способом либо явно вызывайте его инициализацию на старте. Отложенная загрузка не гарантирована при любом запуске: глобальный режим и фильтр импортов могут сделать такой импорт немедленным. `lazy` допускается на уровне модуля, но не внутри функции, класса или блока `try`; `lazy import` также не работает с `*` и `__future__`. Python 3.11–3.14 не разбирает синтаксис `lazy import`. Если один исходник рассчитан на обе ветки, в начале модуля задайте `__lazy_modules__ = ["report_renderer"]` и оставьте обычный `import report_renderer` на уровне модуля. Python 3.15 учитывает этот список для подходящих импортов, а 3.11–3.14 выполняют импорт сразу. Для относительного импорта в списке нужно полное имя модуля. [Python 3.15 Language Reference](https://docs.python.org/3.15/reference/simple_stmts.html#compatibility-via-lazy-modules). [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-810-explicit-lazy-imports), [PEP 810](https://peps.python.org/pep-0810/).
**Граница первого использования.** При `lazy from renderers import PdfRenderer, HtmlRenderer` обращение к `PdfRenderer` загружает модуль целиком и разрешает только имя `PdfRenderer`. Привязка `HtmlRenderer` может остаться отложенной, хотя модуль уже загружен. Проверка имён через `globals()` или `vars(module)` тоже может увидеть прокси вместо класса. Поэтому проверка словаря на старте не доказывает, что оба класса готовы к использованию. Если они нужны сразу, обратитесь к ним по имени или через атрибут модуля до того, как сервис объявит себя готовым. [PEP 810, Reification](https://peps.python.org/pep-0810/#reification).


## Q12. Как профилировать работающий Python-сервис в 3.15?

В Python 3.15 модуль `profiling.sampling` может подключиться к уже работающему процессу по PID без изменения кода и перезапуска. Он периодически снимает стеки: результат помогает найти горячие пути, но доли времени являются статистической оценкой, а короткий вызов может не попасть в выборку. Начните с окна, в котором воспроизводится задержка, и сопоставьте профиль с метриками запроса. [Python `profiling.sampling`](https://docs.python.org/3.15/library/profiling.sampling.html), [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-799-a-dedicated-profiling-package).

```bash
# PID нужного Python worker; профилирование в течение 30 секунд
python3.15 -m profiling.sampling attach --flamegraph -d 30 -o profile.html 12345
```

Режим `wall` по умолчанию включает ожидание I/O и блокировок; `--mode=cpu` оставляет время исполнения на CPU. По умолчанию снимается основной поток; для сервиса с рабочими потоками добавьте `--all-threads`. Для короткого процесса или точного числа вызовов используйте `profiling.tracing` (совместимый интерфейс `cProfile`) при запуске программы: статистическая выборка может оказаться слишком малой. `run` запускает новый процесс, `attach` подключается к существующему, а `dump` даёт один снимок стека, не профиль за интервал. Для `attach` нужны права доступа к памяти целевого процесса; в многопроцессном сервисе выберите нужный worker или профилируйте несколько PID. Профайлер и целевой процесс должны использовать одну minor-версию Python и одинаковый тип сборки — обычный или free-threaded. Для alpha/beta/RC должна совпадать точная версия. Поэтому команда выше рассчитана на совместимый процесс Python 3.15, а не на произвольный Python PID. [Совместимость версий профайлера](https://docs.python.org/3.15/library/profiling.sampling.html#version-compatibility). [Python `profiling.sampling`: commands and modes](https://docs.python.org/3.15/library/profiling.sampling.html), [Python `profiling.tracing`](https://docs.python.org/3.15/library/profiling.tracing.html).

Если подозреваете работу внутри C-расширения или нативной библиотеки, `profiling.sampling --native` покажет синтетические кадры `<native>` в выборке. Для **полного стека C-вызовов** нужен системный профайлер. CPython 3.15 на поддерживаемых платформах собирается с frame pointers по умолчанию: это помогает `perf`, eBPF-инструментам и отладчикам разворачивать стек. Для имён Python-функций в выводе `perf` одного наличия frame pointers недостаточно — отдельно включите поддержку perf map (`-X perf` или `PYTHONPERFSUPPORT=1` при старте процесса). Проверяйте фактическую сборку интерпретатора и расширений: сборка с `--without-frame-pointers` или нативный модуль без соответствующих флагов могут разорвать цепочку развёртки. На Windows x64 развёртка опирается на unwind metadata, а не на эту цепочку. Встроенный `profiling.sampling attach` решает другую задачу и не требует включения perf map. [Python `profiling.sampling`](https://docs.python.org/3.15/library/profiling.sampling.html), [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-831-frame-pointers-enabled-by-default), [perf profiling HOWTO](https://docs.python.org/3/howto/perf_profiling.html), [PEP 831](https://peps.python.org/pep-0831/).

---

## See also

- [Конкурентность в Python](python-concurrency-interview.md) — задачи, отмена, потоки и процессы.
- [Типизация в Python](python-typing-interview.md) — статический контракт и граница runtime-валидации.
- [Упаковка Python-приложений](python-packaging-interview.md) — сборка и повторяемая установка.
- [SQL](../../databases/sql-interview.md) — общие принципы запросов и транзакций.
- [Профилирование приложений](../../performance/application-profiling-interview.md) — выбор метода и чтение профиля.
