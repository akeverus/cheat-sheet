---
title: "Вопросы на собеседовании: Property-based Testing"
description: "Property-based testing: generate test cases automatically, properties vs examples, QuickCheck, Hypothesis (Python), jqwik (Java), shrinking, vs example-based, integration"
tags:
  - interview
  - testing
  - property-based-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Property-based Testing"
  - "QuickCheck interview"
  - "Hypothesis interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Property-based Testing`

`Property-based testing (PBT)` — подход к тестированию, где тесты описывают **свойства** (properties), которым код должен удовлетворять, а фреймворк **генерирует входные данные автоматически**. Зародился в **QuickCheck** (Haskell, 2000). Современные инструменты: **Hypothesis** (Python), **jqwik** (Java), **fast-check** (JS), **PropEr** (Erlang). Дополняет example-based-тестирование.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Hypothesis (Python)](https://hypothesis.readthedocs.io/)
- [QuickCheck Paper](https://www.cs.tufts.edu/~nr/cs257/archive/john-hughes/quick.pdf)
- [jqwik (Java)](https://jqwik.net/)
- [fast-check (JavaScript)](https://fast-check.dev/)
- [Property-Based Testing — F# for Fun and Profit](https://fsharpforfunandprofit.com/posts/property-based-testing/)
- [Hypothesis — Awesome Python](https://github.com/HypothesisWorks/hypothesis)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое property-based testing?](#q1--что-такое-property-based-testing)
- [Q2. (!) Property-based vs example-based?](#q2--property-based-vs-example-based)
- [Q3. (!) Зачем PBT?](#q3--зачем-pbt)

**Properties**
- [Q4. (!) Какие properties testable?](#q4--какие-properties-testable)
- [Q5. Round-trip property?](#q5-round-trip-property)
- [Q6. Idempotency property?](#q6-idempotency-property)
- [Q7. Invariant properties?](#q7-invariant-properties)

**Tools**
- [Q8. (!) QuickCheck (originalpioneer)?](#q8--quickcheck-originalpioneer)
- [Q9. (!) Hypothesis (Python)?](#q9--hypothesis-python)
- [Q10. (!) jqwik (Java)?](#q10--jqwik-java)
- [Q11. fast-check (JavaScript)?](#q11-fast-check-javascript)

**Generators**
- [Q12. (!) Что такое generator?](#q12--что-такое-generator)
- [Q13. Composing generators?](#q13-composing-generators)
- [Q14. Custom generators?](#q14-custom-generators)

**Shrinking**
- [Q15. (!) Что такое shrinking?](#q15--что-такое-shrinking)
- [Q16. Зачем shrinking важен?](#q16-зачем-shrinking-важен)

**Practice**
- [Q17. (!) Examples reverse list, sorting, parsing?](#q17--examples-reverse-list-sorting-parsing)
- [Q18. Test integration с standard tests?](#q18-test-integration-с-standard-tests)
- [Q19. Stateful PBT (model-based)?](#q19-stateful-pbt-model-based)

**Production**
- [Q20. (!) Когда применять PBT?](#q20--когда-применять-pbt)
- [Q21. (!) Limitations и criticism?](#q21--limitations-и-criticism)

## Q1. (!) Что такое property-based testing?

(!) Что такое property-based testing?

**Property-based testing (PBT)** — подход к тестированию:

1. **Определяешь свойство (property)** — инвариант, которому код должен удовлетворять (для всех входов)
2. Фреймворк **генерирует много входов** случайным образом
3. Свойство проверяется для каждого входа
4. Если падает — **shrink** до минимального контрпримера

**Пример:**
```python
@given(st.lists(st.integers()))
def test_reverse_twice_returns_original(lst):
    assert reverse(reverse(lst)) == lst
```

Hypothesis (Python) генерирует сотни случайных списков и проверяет свойство.

**Если падает** — Hypothesis сжимает (shrink) до минимального падающего случая (например, `[1, 0]`).

## Q2. (!) Property-based vs example-based?

**Example-based** (традиционный):
```python
def test_reverse():
    assert reverse([1, 2, 3]) == [3, 2, 1]
    assert reverse([]) == []
    assert reverse([1]) == [1]
```

Ты пишешь **конкретные входы + ожидаемые выходы**.

**Property-based:**
```python
@given(st.lists(st.integers()))
def test_reverse_idempotent(lst):
    assert reverse(reverse(lst)) == lst  # property
```

Ты описываешь **что должно быть истинно**, а фреймворк сам подбирает входы.

**Оба подхода дополняют друг друга.** Примеры — для известных случаев, свойства — для общих инвариантов.

## Q3. (!) Зачем PBT?

**Причины:**

1. **Находит граничные случаи**, о которых ты не подумал (пустой список, очень большие числа, unicode, …)
2. **Больше покрытия** при меньшем коде (один property-тест = сотни примеров)
3. **Заставляет думать в терминах свойств** — улучшает дизайн кода
4. **Ловит регрессии** (падающие входы сохраняются для повторного прогона)
5. **Лучше fuzz-тестирования** — у него есть проверки (assertions)

**Известные находки** через PBT:
- Баги в стандартных библиотеках (Java Collections)
- Уязвимости в криптографии
- Несогласованности в файловых системах

## Q4. (!) Какие properties testable?

**Типовые категории:**

1. **Round-trip:** `decode(encode(x)) == x`
2. **Идемпотентность:** `f(f(x)) == f(x)`
3. **Инварианты:** размер, сумма, отсортированность и т.д.
4. **Сравнение с эталоном:** `optimized(x) == naive(x)`
5. **Математические свойства:** коммутативность, ассоциативность, дистрибутивность
6. **Известные ответы:** `f(empty) == empty`
7. **Стабильность:** результат не меняется при повторной сортировке

**Для любой функции** найдутся свойства.

## Q5. Round-trip property?

**`decode(encode(x)) == x`** для всех x.

```python
@given(st.dictionaries(st.text(), st.integers()))
def test_json_roundtrip(d):
    assert json.loads(json.dumps(d)) == d
```

**Сценарии применения:**
- Сериализация (JSON, Protobuf, XML)
- Кодирование (Base64, URL encoding)
- Сжатие (`decompress(compress(x)) == x`)
- Шифрование (`decrypt(encrypt(x, key), key) == x`)

**Найденные баги:** JSON-библиотеки, ломающиеся на unicode и специальных float-значениях.

## Q6. Idempotency property?

**`f(f(x)) == f(x)`** — вызов дважды = вызову один раз.

```python
@given(st.lists(st.integers()))
def test_sort_idempotent(lst):
    assert sorted(sorted(lst)) == sorted(lst)
```

**Сценарии применения:**
- Сортировка
- Нормализация (канонические формы)
- Операции очистки (cleanup)

## Q7. Invariant properties?

**Свойство выполняется для всех входов.**

```python
@given(st.lists(st.integers()))
def test_sort_preserves_size(lst):
    assert len(sorted(lst)) == len(lst)

@given(st.lists(st.integers()))
def test_sort_preserves_sum(lst):
    assert sum(sorted(lst)) == sum(lst)
```

**Несколько инвариантов** ловят разные аспекты поведения.

## Q8. (!) QuickCheck (originalpioneer)?

**QuickCheck** — первый PBT-фреймворк (Haskell, 2000, Koen Claessen и John Hughes).

```haskell
prop_reverse :: [Int] -> Bool
prop_reverse xs = reverse (reverse xs) == xs

main = quickCheck prop_reverse
-- +++ OK, passed 100 tests.
```

**Повлиял** на каждый современный PBT-фреймворк. **Основополагающая статья** до сих пор широко цитируется.

**Порты на другие языки:**
- ScalaCheck (Scala)
- ScalaCheck для Java
- jqwik (Java, осовременённый)
- Hypothesis (Python)
- fast-check (JS)
- proptest (Rust)
- gopter (Go)

## Q9. (!) Hypothesis (Python)?

**Hypothesis** — PBT-фреймворк для Python. Самый популярный и зрелый.

```python
from hypothesis import given, strategies as st

@given(st.text())
def test_uppercase_lowercase_inverse(s):
    assert s == s.upper().lower() or contains_special(s)

@given(st.integers(min_value=1, max_value=100))
def test_factorial_positive(n):
    assert factorial(n) > 0

@given(st.dictionaries(
    keys=st.text(min_size=1),
    values=st.integers()
))
def test_dict_operations(d):
    assert len(d) >= 0
    assert all(isinstance(k, str) for k in d.keys())
```

**Стратегии (`st.*`)** — встроенные генераторы для распространённых типов.

**База примеров (examples database)** — падающие случаи сохраняются и повторно прогоняются при следующем запуске.

## Q10. (!) jqwik (Java)?

**jqwik** — современный PBT для Java (заменяет QuickCheck-Java).

```java
@Property
boolean concatenationLength(@ForAll List<Integer> a, @ForAll List<Integer> b) {
    return concat(a, b).size() == a.size() + b.size();
}

@Property
@StatisticsReport
void sortIsIdempotent(@ForAll @Size(max = 100) List<Integer> list) {
    List<Integer> sorted = new ArrayList<>(list);
    Collections.sort(sorted);
    List<Integer> sortedTwice = new ArrayList<>(sorted);
    Collections.sort(sortedTwice);

    Assertions.assertThat(sortedTwice).isEqualTo(sorted);
}
```

**Аннотации:**
- `@Property` — метод-свойство
- `@ForAll` — сгенерировать значение
- `@Size`, `@IntRange` — ограничить генераторы

**Совместим с JUnit 5** — работает рядом с методами `@Test`.

## Q11. fast-check (JavaScript)?

```javascript
import fc from 'fast-check';

test('reverse is involutive', () => {
  fc.assert(
    fc.property(fc.array(fc.integer()), (data) => {
      return reverse(reverse(data)).join(',') === data.join(',');
    })
  );
});
```

**Современный и популярный** в экосистеме JS.

**Используют:** ESLint, Babel, Apollo и крупные JS-проекты.

## Q12. (!) Что такое generator?

**Генератор (generator)** = функция, производящая случайные значения.

**Встроенные генераторы (Hypothesis):**
- `st.integers()`, `st.floats()`
- `st.text()`, `st.binary()`
- `st.booleans()`
- `st.lists(elements_strategy)`
- `st.dictionaries(keys_strategy, values_strategy)`
- `st.tuples(strat1, strat2)`
- `st.dates()`, `st.datetimes()`
- `st.from_regex("[a-z]{3,5}")`

**Ограничения:**
```python
st.integers(min_value=0, max_value=100)
st.lists(st.integers(), min_size=1, max_size=10)
st.text(alphabet="abc", max_size=5)
```

## Q13. Composing generators?

**Комбинируй генераторы** для сложных типов:

```python
# Generate User objects
user_strategy = st.builds(
    User,
    name=st.text(min_size=1, max_size=50),
    age=st.integers(min_value=0, max_value=150),
    email=st.emails()
)

@given(user_strategy)
def test_user_validation(user):
    assert validate(user)

# Lists of users
@given(st.lists(user_strategy, min_size=1, max_size=100))
def test_user_collection(users):
    ...
```

**`flatmap`** — генератор, зависящий от другого:
```python
st.integers().flatmap(lambda n: st.lists(st.integers(), min_size=n, max_size=n))
# Lists of randomly chosen size
```

## Q14. Custom generators?

**Для доменных типов:**

```python
@st.composite
def valid_isbn(draw):
    digits = draw(st.lists(st.integers(0, 9), min_size=12, max_size=12))
    check_digit = compute_isbn_check(digits)
    return ''.join(map(str, digits)) + str(check_digit)

@given(valid_isbn())
def test_isbn_validates(isbn):
    assert is_valid_isbn(isbn)
```

**Свои генераторы** нужны для:
- Доменно-специфичных строк (UUID, ISBN)
- Сложных валидных объектов
- Машин состояний (для stateful PBT)

## Q15. (!) Что такое shrinking?

**Shrinking** — когда свойство падает, фреймворк **упрощает** падающий вход.

**Пример:**
```python
@given(st.lists(st.integers()))
def test_sort_property(lst):
    assert sorted(lst)[-1] >= sorted(lst)[0]  # max >= min
```

**Баг:** падает на пустом списке (IndexError).

**Без shrinking:** в отчёте о падении видно `[42, -7, 100, 13, ...]` (случайный)
**С shrinking:** Hypothesis упрощает → сообщает о падении на `[]`.

**Процесс:**
1. Найден падающий вход
2. Пробуем уменьшенные версии (короче список, меньше числа)
3. Пока сжимать дальше нельзя
4. Сообщаем **минимальный контрпример**

## Q16. Зачем shrinking важен?

**Без shrinking:** отлаживать тяжело («почему падает `[42, -7, 100, ...]`?»).

**С shrinking:** **причина очевидна** («пустой список — случай не обработан»).

**Современные PBT** используют изощрённые алгоритмы shrinking.

**Hypothesis особенно хорош** — сжимает сложные типы (словари, кастомные объекты).

## Q17. (!) Examples reverse list, sorting, parsing?

**Reverse (разворот):**
```python
@given(st.lists(st.integers()))
def test_reverse(lst):
    assert reverse(reverse(lst)) == lst
    assert len(reverse(lst)) == len(lst)
    if lst:
        assert reverse(lst)[0] == lst[-1]
```

**Sorting (сортировка):**
```python
@given(st.lists(st.integers()))
def test_sort(lst):
    sorted_lst = sorted(lst)
    # Properties
    assert len(sorted_lst) == len(lst)
    assert set(sorted_lst) == set(lst)  # same elements
    assert all(sorted_lst[i] <= sorted_lst[i+1] for i in range(len(sorted_lst)-1))
```

**Parsing (round-trip):**
```python
@given(st.dictionaries(st.text(), st.integers()))
def test_json(d):
    assert json.loads(json.dumps(d)) == d
```

## Q18. Test integration с standard tests?

**PBT рядом с example-тестами:**

```python
class TestSort:
    # Example tests (specific known cases)
    def test_empty(self):
        assert sort([]) == []

    def test_already_sorted(self):
        assert sort([1, 2, 3]) == [1, 2, 3]

    # Property test (general invariants)
    @given(st.lists(st.integers()))
    def test_invariants(self, lst):
        result = sort(lst)
        assert sorted(lst) == result
```

**Best practice:**
- **Примеры** — для известных граничных случаев (конкретная документация)
- **Свойства** — для общих инвариантов

## Q19. Stateful PBT (model-based)?

**Генерируем последовательности операций** и проверяем, что состояние остаётся согласованным.

```python
from hypothesis.stateful import RuleBasedStateMachine, rule

class StackMachine(RuleBasedStateMachine):
    def __init__(self):
        super().__init__()
        self.stack = Stack()
        self.model = []

    @rule(value=st.integers())
    def push(self, value):
        self.stack.push(value)
        self.model.append(value)

    @rule()
    def pop(self):
        if self.model:
            assert self.stack.pop() == self.model.pop()

    @invariant()
    def size_matches(self):
        assert self.stack.size() == len(self.model)
```

**Hypothesis** генерирует последовательности (push, push, pop, pop, push, …) — проверяет соответствие модели.

**Найдены баги в:** базах данных, распределённых системах, сложных машинах состояний.

## Q20. (!) Когда применять PBT?

**Хорошо подходит:**
- **Чистые функции** (сортировка, парсинг, кодирование)
- **Операции над структурами данных**
- **Математика, алгоритмы**
- **Сериализация**
- **Машины состояний**
- **Код библиотек** (которым пользуются многие)

**Хуже подходит:**
- **UI-код** (тяжело сформулировать свойства)
- **Жёстко связанный с внешним** (БД, сеть)
- **С большим числом сайд-эффектов** (нужен аккуратный дизайн)

**Комбинируй PBT + example-тестирование** — PBT не замена.

## Q21. (!) Limitations и criticism?

1. **Сложно проектировать свойства** — что именно тестировать?
2. **Ложное ощущение покрытия** — свойства могут пропустить проблемы
3. **Медленно** для сложных генераторов
4. **Генераторы несовершенны** — могут не покрывать реальное распределение данных
5. **Падения иногда тяжело отлаживать**
6. **Подходит не для всего кода** (UI, интеграционные тесты)
7. **Порог входа** — другой образ мышления
8. **Поддержка** — свойства эволюционируют вместе с кодом

**Не серебряная пуля.** Дополняет другие виды тестирования.

В **2025** PBT набирает популярность в командах, ориентированных на качество. **Hypothesis** очень популярен в мире Python.

## See also

- [Mutation Testing](mutation-testing-interview.md) — также quality-focused
- [Test Strategies](test-strategies-interview.md)
- [Load Testing](load-testing-interview.md)
- [Chaos Engineering](chaos-engineering-interview.md) — similar mindset (find unknowns)
- [[code-quality-interview|Code Quality]]
- [Mockito](mockito-interview.md) — Java mocking
- [Integration Testing](integration-testing-interview.md)
- [Scala](../programming-languages/scala/scala-interview.md) — ScalaCheck
- [[python-interview|Python]] — Hypothesis
